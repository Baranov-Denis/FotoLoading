package Model;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Model {
    private String sourcePath;
    private String destinationPath;

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public void readSettings() {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream("setting.txt"))) {
            sourcePath = inputStream.readUTF();
            destinationPath = inputStream.readUTF();
        } catch (Exception e) {
            System.out.println("Exception in readSetting()");
        }
    }

    public void writeSettings(String source, String destination) {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream("setting.txt"))) {
            outputStream.writeUTF(source);
            outputStream.writeUTF(destination);
        } catch (Exception f) {
            System.out.println("Exception in writeSetting()");
        }
    }


    public void startCopyFilesProcess(String sourcePath) {
        searchingFiles(sourcePath);
    }


    private void searchingFiles(String sourcePath) {
        File[] listOfInputFiles = getListOfInputFiles(sourcePath);
        if (listOfInputFiles.length != 0) {

            for (File inputFile : listOfInputFiles) {
                if (!inputFile.isFile()) {
                    searchingFiles(inputFile.getAbsolutePath());
                } else {
                    copyingFile(inputFile);
                }
            }

        } else {
            System.out.println("Files not found");
        }
    }

    private void copyingFile(File file) {
        System.out.println(file.getName());
        String[] allOfFileInfo = getInfoOfFile(file);

        if (allOfFileInfo[0] != null) {
            String newDestinationString = createNewDestinationPath(allOfFileInfo);
            // Path newDestinationPath = Paths.get(newDestinationString);
            try {
                checkExistingDirectory(newDestinationString);
                //if (Files.exists(newDestinationPath.resolve(file.getName()))) {

                //    File tempFile = new File(createNewNameForRepeatingFile(file));
                //  if(Files.exists(newDestinationPath.resolve(file.getName()))) {
                newDestinationString = getFinalDestinationPath(file, newDestinationString);
                testingExisting(file, newDestinationString);
                //  }

                //    Files.copy(file.toPath(), newDestinationPath.resolve(file.getName()),
                //          StandardCopyOption.REPLACE_EXISTING);
                //  }


                //   System.out.println("++++++++++++++++++++++++"+filehh.getName());

                //    Files.copy(file.toPath(), newDestinationPath.resolve(file.getName()),
                //       StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception t) {
                System.out.println("Exception in copying");
            }
        }
    }

  /*/  private  void testingExsisting(File file,Path destinationPath) throws Exception{
        File tempFile = file ;
        if (Files.exists(destinationPath.resolve(file.getName()))) {
            tempFile = new File(createNewNameForRepeatingFile(file));
            System.out.println("---------------------"+tempFile.getName());
            testingExsisting(tempFile,destinationPath);
            Files.copy(file.toPath(), destinationPath.resolve(tempFile.getName()),
                    StandardCopyOption.REPLACE_EXISTING);



        }else{
            Files.copy(file.toPath(), destinationPath.resolve(tempFile.getName()),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("==8888888888888888888888888888888888888");
        }

    }*/

    private void testingExisting(File file, String destinationPath)  {
        System.out.println(destinationPath);
        //if (Files.exists(Paths.get(destinationPath).resolve(file.getName()))) {
            if (Files.exists(Paths.get(destinationPath))) {
            destinationPath = createNewNameForRepeatingFile(destinationPath);
            testingExisting(file, destinationPath);
        } else {

            writeFileToDestination(file, destinationPath);
        }

    }

    private void writeFileToDestination(File file, String destinationPath) {
        try {
            Files.copy(file.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            System.out.println("Writing file failed");
        }
    }

    private String getFinalDestinationPath(File file, String destinationPath) {
        return destinationPath + file.getName();
    }


    private String createNewNameForRepeatingFile(String destinationPath) {
        String[] tempFileName = destinationPath.split("\\.");

        String newFileName = /*sourcePath + "\\" +*/ tempFileName[0] + "-1" + "." + tempFileName[1];
        System.out.println("==========================" + newFileName);
        return newFileName;
    }

    private void checkExistingDirectory(String destination) {
        File newFile = new File(destination);
        if (!newFile.exists()) {
            createNewDirectory(newFile);
        }
    }

    private void createNewDirectory(File file) {
        if (!file.mkdirs()) System.out.println("Directory didn't created");
    }


    private String[] getInfoOfFile(File file) {
        String dateOfFile = "";
        String typeOfFile = "";
        String[] allInfo = new String[4];

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);

            for (Directory directory : metadata.getDirectories()) {


                for (Tag tag : directory.getTags()) {

                    if (tag.toString().contains("[File] File Name")) {
                        String[] testName = tag.toString().split("\\.");
                        if (testName.length != 2) {
                            break;
                        }
                    }


                    if (tag.toString().contains("File Modified Date")) {
                        dateOfFile = tag.toString();


                    }
                    if (tag.toString().contains("Detected File Type Name")) {
                        typeOfFile = tag.toString();
                    }

                }
            }

            //   System.out.println(typeOfFile);

            String[] tempAllDates = dateOfFile.substring(31).split(" ");

            String[] type = typeOfFile.split(" ");

            allInfo[0] = type[type.length - 1].trim();
            allInfo[1] = tempAllDates[1].trim();
            allInfo[2] = replaceMonthName(tempAllDates[0].replace(".", ""));
            allInfo[3] = tempAllDates[4].trim();

        } catch (Exception e) {
            System.out.println("Exception in getInfoOfFile(File file) method");
        }

        return allInfo;
    }

    private String replaceMonthName(String month) {
        switch (month) {
            case ("янв"):
                return "Январь";
            case ("февр"):
                return "Февраль";
            case ("мар"):
                return "Март";
            case ("апр"):
                return "Апрель";
            case ("мая"):
                return "Май";
            case ("июн"):
                return "Июнь";
            case ("июл"):
                return "Июль";
            case ("авг"):
                return "Август";
            case ("сент"):
                return "Сентябрь";
            case ("окт"):
                return "Октябрь";
            case ("нояб"):
                return "Ноябрь";
            case ("дек"):
                return "Декабрь";
        }
        return "Unknown";
    }

    private File[] getListOfInputFiles(String sourcePath) {
        File sourceFolder = new File(sourcePath);
        return sourceFolder.listFiles();
    }


    private String createNewDestinationPath(String[] fileInfo) {
        return destinationPath + "\\" + fileInfo[0] + "\\" + fileInfo[3] + "\\" + fileInfo[2] + " " + fileInfo[1] + "\\";
    }
}

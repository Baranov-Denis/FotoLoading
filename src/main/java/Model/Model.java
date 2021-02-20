package Model;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Model {
    private String sourcePath;
    private String destinationPath;
    private boolean continueExecute = true;


    public void setContinueExecute(boolean continueExecute) {
        this.continueExecute = continueExecute;
    }

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
            sourcePath = "D:\\";
            destinationPath = "D:\\";
            writeSettings(sourcePath,destinationPath);
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


    public void startCopyingFilesProcess(String sourcePath) {
        searchingInputFiles(sourcePath);
    }



    File[] listOfInputFiles;

    private void searchingInputFiles(String sourcePath) {

        listOfInputFiles = getListOfInputFiles(sourcePath);

        if (listOfInputFiles.length != 0) {
            for (File inputFile : listOfInputFiles) {
                if (!inputFile.isFile()) {
                    searchingInputFiles(inputFile.getAbsolutePath());
                } else {
                    if(continueExecute) {
                        copyingFile(inputFile);
                    }
                }
            }
        } else {
            System.out.println("Files not found");
        }
    }

    private File[] getListOfInputFiles(String sourcePath) {
        File sourceFolder = new File(sourcePath);
        return sourceFolder.listFiles();
    }


    private void copyingFile(File file) {

        String[] allOfFileInfo = getInfoOfFile(file);

        if (allOfFileInfo[0] != null) {

            String destinationPathWithoutFileName = createNewDestinationPathWithoutFileName(allOfFileInfo);

            checkExistingDirectory(destinationPathWithoutFileName);
            String destinationPathWithFileName = getFinalDestinationPath(file, destinationPathWithoutFileName);
            checkingForFilesWithDuplicateNames(file, destinationPathWithFileName);

        }
    }

    private String createNewDestinationPathWithoutFileName(String[] fileInfo) {
        return destinationPath + "\\" + fileInfo[0] + "\\" + fileInfo[3] + "\\" + fileInfo[3] + " " + fileInfo[2] +
                " " + fileInfo[1] +
                "\\";
    }


    private void checkingForFilesWithDuplicateNames(File file, String destinationPath)  {

        if (Files.exists(Paths.get(destinationPath))) {

            int inp = JOptionPane.showConfirmDialog(new JPanel(),
                    "Do you want to change name this file : " + file.getName() + " " +
                    "?");
            if(inp == 0) {
                destinationPath = createNewNameForRepeatingFile(destinationPath);
                checkingForFilesWithDuplicateNames(file, destinationPath);
            }else if(inp == 2){
continueExecute = false;
            }
        } else {
            writeFileToDestination(file, destinationPath);
        }
    }

    private void writeFileToDestination(File file, String destinationPath) {
        try {
            Files.copy(file.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            System.out.println("Writing file failed!");
        }
    }

    private String getFinalDestinationPath(File file, String destinationPath) {
        return destinationPath + file.getName();
    }


    private String createNewNameForRepeatingFile(String destinationPath) {
        String[] fileNameSplitByPoint = destinationPath.split("\\.");
        String[] fileNameSplitBySlash = fileNameSplitByPoint[0].split("\\\\");
        String newFileName;
        if (!fileNameSplitBySlash[fileNameSplitBySlash.length-1].contains("Duplicate")) {
            newFileName =
                    fileNameSplitByPoint[0] + "__Duplicate-1"  +
                            "." + fileNameSplitByPoint[1];
        }else {
            String[] splitByDuplicate = fileNameSplitByPoint[0].split("__Duplicate-");
            int countOfDuplicate = Integer.parseInt(splitByDuplicate[1]);
            newFileName = splitByDuplicate[0]  + "__Duplicate-"  + ++countOfDuplicate +
                    "." + fileNameSplitByPoint[1];
        }
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


}

package Model;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Model {
    private String sourcePath = "H:\\input";

    private String destinationPath = "H:\\output";

    private static String createDest(File photo) throws Exception {
        String destPath;
        String tempPath = "H:\\out2\\";
        String tempYear = "";
        Metadata metadata = ImageMetadataReader.readMetadata(photo);
        Object[] fff = null;
        for (Directory directory : metadata.getDirectories()) {
            fff = directory.getTags().toArray();
        }
        try {
            String tempInfo = fff[fff.length - 1].toString();
            tempYear = tempInfo.substring(tempInfo.length() - 4);
            System.out.println("----------------- " + tempYear);
        } catch (Exception e) {
        }
        System.out.println(tempPath + tempYear);
        return tempPath + tempYear;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void copyDir(String sourceDirName, String targetSourceDir) throws Exception {


        //Создаю обьект файл в который передаётся путь к папке с файлами,
        // которые нужно скопировать
        File folder = new File(sourceDirName);
        //Получаю список имён всех файлов из данной папки
        File[] listOfFiles = folder.listFiles();


        //Path destDir = Paths.get(targetSourceDir);

        String name;


        if (listOfFiles != null) {
            for (File file : listOfFiles) {

                name = file.getName();
                name = name.substring(name.length() - 3);

                if (name.equals(/*"jpg"*/"CR2")) {
                    //  createDest(file);
                    Path destDir = Paths.get(createDest(file));
                    File theDir = new File(createDest(file));
                    if (!theDir.exists()) {
                        theDir.mkdirs();
                    }
                    Files.copy(file.toPath(), destDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                }
                if (!file.getName().matches(".+[.].+")) {

                    System.out.println(file.getName() + "   " + file.getName().matches(".+[.].+"));
                    copyDir(file.getAbsolutePath(), targetSourceDir);
                }
            }
        }


    }


    public void copy(String sourcePath) {

        File[] listOfInputFiles = getListOfInputFiles(sourcePath);


        if (listOfInputFiles.length != 0) {
            for (File inputFile : listOfInputFiles) {
                if (!inputFile.isFile()) {
                    copy(inputFile.getAbsolutePath());
                } else {

                    System.out.println();
                    System.out.println("-------------------------*******************************************************");
                    System.out.println(inputFile.getName());


                    String[] allOfFileInfo = getInfoOfFile(inputFile);
                    //  System.out.println();
                    //   System.out.print(inputFile.getName() + " ------ ");
                    //    for (String dates : allOfFileInfo) System.out.print(dates + " ----- ");


                    if (allOfFileInfo[0] != null) {
                        String newDestinationPath = createNewDestinationPath(allOfFileInfo);
                          System.out.println(newDestinationPath);
                        Path ou = Paths.get(newDestinationPath);
                        try {
                            File theDir = new File(newDestinationPath);
                            if (!theDir.exists()) {
                                theDir.mkdirs();
                            }
                            Files.copy(inputFile.toPath(), ou.resolve(inputFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception t) {
                            System.out.println("Exception in copiyng");
                        }
                    }
                }
            }

        } else {
            System.out.println("Files not found");
        }


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
                        if(testName.length != 2) {
                            break;
                        }
                    }


                            if (tag.toString().contains("File Modified Date")) {
                                dateOfFile = tag.toString();
                                System.out.println(dateOfFile);

                                System.out.println(typeOfFile);

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

    private File getInputFile(File[] listOfInputFiles) {

        return null;
    }

    private String createNewDestinationPath(String[] fileInfo) {
        String newDestinationPath;
        newDestinationPath =
                destinationPath + "\\" + fileInfo[0] + "\\" + fileInfo[3] + "\\" + fileInfo[2] + " " + fileInfo[1] +
                        "\\";
        return newDestinationPath;
    }
}

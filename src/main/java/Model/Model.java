package Model;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class Model implements Runnable {
    private final ArrayList<InputFile> listOfSourceFilesForCopying = new ArrayList<>();
    private String sourcePath;
    private String destinationPath;
    private boolean operationContinues = true;
    private int numberOfDonePhotos;
    private long inputFilesLength;


    //--------------------------------------------------- Getters and Setters ---------------------------------
    //---------------------------------------------------------------------------------------------------------


    public void setNumberOfDonePhotos(int numberOfDonePhotos) {
        this.numberOfDonePhotos = numberOfDonePhotos;
    }

    public void setOperationContinues(boolean operationContinues) {
        this.operationContinues = operationContinues;
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

    //----------------------------------------------- Read and Write Settings ---------------------------------
    //---------------------------------------------------------------------------------------------------------

    public void readSettings() {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream("setting.txt"))) {
            sourcePath = inputStream.readUTF();
            destinationPath = inputStream.readUTF();
        } catch (Exception e) {
            System.out.println("Exception in readSetting()");
            sourcePath = "D:\\";
            destinationPath = "D:\\";
            writeSettings(sourcePath, destinationPath);
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

    //---------------------------------------------- Start Method ------------------------------------------------
    //------------------------------------------------------------------------------------------------------------


    public void run() {
        getListOfRawInputFilesFromSourcePath(sourcePath);
        if (thereIsEnoughFreeSpaceForCopying()) {

            for (InputFile file : listOfSourceFilesForCopying) {
                if (operationContinues) {
                    getInfoOfFile(file);
                    System.out.println(file.getDestinationPathWithFileName());
                    copyingFile(file);
                    numberOfDonePhotos++;
                } else {

                }
            }
        } else {

        }
    }

    //-------------------------------------- Get Percent of Done -------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    public int getProcessOfDone() {
        return ((numberOfDonePhotos * 100) / listOfSourceFilesForCopying.size());
    }


    private boolean thereIsEnoughFreeSpaceForCopying() {
        File destFolder = new File(destinationPath);
        long destinationFreeSize = destFolder.getFreeSpace();
        return destinationFreeSize > inputFilesLength;
    }


    //------------------------------------- Get List Of Input Files ----------------------------------------------
    //------------------------------------------------------------------------------------------------------------


    private void getListOfRawInputFilesFromSourcePath(String sourcePath) {

        File[] listOfRawInputFiles = getListOfInputFiles(sourcePath);

        if (listOfRawInputFiles.length != 0) {
            for (File rawInputFile : listOfRawInputFiles) {
                if (!rawInputFile.isFile()) {
                    getListOfRawInputFilesFromSourcePath(rawInputFile.getAbsolutePath());
                } else {
                    listOfSourceFilesForCopying.add(new InputFile(rawInputFile, destinationPath));
                    inputFilesLength += rawInputFile.length();
                }
            }
        }
    }


    private File[] getListOfInputFiles(String sourcePath) {
        File sourceFolder = new File(sourcePath);
        return sourceFolder.listFiles();
    }

    //--------------------------------------------------------------------------------------------------------------


    private void copyingFile(InputFile file) {


        //  System.out.println("******* "+file.getName());
        if (!(file.getName() == null)) {
            checkExistingDirectory(file.getDestinationPathWithoutFileName());
            checkingForFilesWithDuplicateNames(file, file.getDestinationPathWithFileName());

        }
    }


    private void checkingForFilesWithDuplicateNames(InputFile file, String destinationPath) {


        if (Files.exists(Paths.get(destinationPath))) {

            InputFile existFile = new InputFile(new File(destinationPath), destinationPath);
            getInfoOfFile(existFile);

            if (!existFile.equals(file)) {

                destinationPath = createNewNameForRepeatingFile(destinationPath);
                checkingForFilesWithDuplicateNames(file, destinationPath);
            }

        } else {
            writeFileToDestination(file.getFile(), destinationPath);
        }
    }

    private void writeFileToDestination(File file, String destinationPath) {
        try {
            Files.copy(file.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            System.out.println("Writing file failed!");
        }
    }


    private String createNewNameForRepeatingFile(String destinationPath) {
        String[] fileNameSplitByPoint = destinationPath.split("\\.");
        String[] fileNameSplitBySlash = fileNameSplitByPoint[0].split("\\\\");
        String newFileName;
        if (!fileNameSplitBySlash[fileNameSplitBySlash.length - 1].contains("Duplicate")) {
            newFileName =
                    fileNameSplitByPoint[0] + "__Duplicate-1" +
                            "." + fileNameSplitByPoint[1];
        } else {
            String[] splitByDuplicate = fileNameSplitByPoint[0].split("__Duplicate-");
            int countOfDuplicate = Integer.parseInt(splitByDuplicate[1]);
            newFileName = splitByDuplicate[0] + "__Duplicate-" + ++countOfDuplicate +
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

    private void getInfoOfFile(InputFile file) {

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getFile());

            for (Directory directory : metadata.getDirectories()) {


                for (Tag tag : directory.getTags()) {

                    if (tag.toString().contains("[File] File Name")) {
                        String[] testName = tag.toString().split("\\.");
                        if (testName.length != 2 || testName[1].equals("null")) {
                            break;
                        }
                        file.setName(tag.toString().substring(18).trim());
                    }

                    if (tag.toString().contains("File Modified Date")) {
                        String[] tempAllDates = tag.toString().substring(31).split(" ");
                        file.setTime(tempAllDates[2].trim());
                        file.setDay(tempAllDates[1].trim());
                        file.setMonth(tempAllDates[0].replace(".", ""));
                        file.setYear(tempAllDates[4].trim());
                    }

                    if (tag.toString().contains("Detected File Type Name")) {
                        file.setType(tag.toString().substring(38).trim());
                    }

                }
            }
        } catch (Exception e) {
            // System.out.println("Exception in getInfoOfFile(File file) method");
        }
    }


}

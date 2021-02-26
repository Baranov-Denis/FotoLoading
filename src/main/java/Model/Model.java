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
    private ArrayList<InputFile> listOfSourceInputFilesForCopying;
    private String sourcePathName;
    private String destinationPathName;
    private boolean copyingContinues = true;
    private int numberOfCopiedFiles;
    private long sizeOfSourceFilesForCopy;
    private String messageToViewer;
    private int percentOfDone;


    //--------------------------------------------------- Getters and Setters ---------------------------------
    //---------------------------------------------------------------------------------------------------------


    public int getPercentOfDone() {
        return percentOfDone;
    }

    public void setPercentOfDone(int percentOfDone) {
        this.percentOfDone = percentOfDone;
    }

    public String getMessageToViewer() {
        return messageToViewer;
    }

    public void setMessageToViewer(String messageToViewer) {
        this.messageToViewer = messageToViewer;
    }

    public void setNumberOfCopiedFiles(int numberOfCopiedFiles) {
        this.numberOfCopiedFiles = numberOfCopiedFiles;
    }

    public void setCopyingContinues(boolean copyingContinues) {
        this.copyingContinues = copyingContinues;
    }

    public String getSourcePathName() {
        return sourcePathName;
    }

    public void setSourcePathName(String sourcePathName) {
        this.sourcePathName = sourcePathName;
    }

    public String getDestinationPathName() {
        return destinationPathName;
    }

    public void setDestinationPathName(String destinationPathName) {
        this.destinationPathName = destinationPathName;
    }


    //----------------------------------------------- Read and Write Settings ------------------------------------
    //------------------------------------------------------------------------------------------------------------

    public void readSettings() {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream("setting.txt"))) {
            sourcePathName = inputStream.readUTF();
            destinationPathName = inputStream.readUTF();
        } catch (Exception e) {
            System.out.println("Exception in readSetting()");
            sourcePathName = "D:\\";
            destinationPathName = "D:\\";
            writeSettings(sourcePathName, destinationPathName);
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

        setPercentOfDone(0); //set percent of done = 0 for progress bar = 0%

        listOfSourceInputFilesForCopying = new ArrayList<>(); //Create it here. Because other case in next copying
        // new files will be added to this list and all of them will be processed again.

        getListOfRawInputFilesFromSourcePath(sourcePathName);//We get input files. They have only destination Path
        // Name. No other information.

        if (thereIsEnoughFreeSpaceForCopying()) {
            for (InputFile file : listOfSourceInputFilesForCopying) {
                if (copyingContinues) {
                    getInfoOfFile(file);
                    System.out.println(file.getDestinationPathWithFileName());
                    copyingFile(file);
                    getProcessOfDone();
                    numberOfCopiedFiles++;
                }
            }
        } else {
            setPercentOfDone(100);
            setMessageToViewer("Space is not Enough");
            return;
        }
        setPercentOfDone(100);
        setMessageToViewer("All Photo were copy");
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
                    getListOfSourceInputFilesForCopying(new InputFile(rawInputFile, destinationPathName));
                    setSizeOfSourceFilesForCopy(rawInputFile);
                }
            }
        }
    }

    private void getListOfSourceInputFilesForCopying(InputFile inputFile) {
        listOfSourceInputFilesForCopying.add(inputFile);
    }

    private void setSizeOfSourceFilesForCopy(File file) {
        sizeOfSourceFilesForCopy += file.length();
    }

    private File[] getListOfInputFiles(String sourcePath) {
        File sourceFolder = new File(sourcePath);
        return sourceFolder.listFiles();
    }


    //-------------------------------------- Check Free space for copying ----------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private boolean thereIsEnoughFreeSpaceForCopying() {
        File destFolder = new File(destinationPathName);
        long destinationFreeSize = destFolder.getFreeSpace();
        return destinationFreeSize > sizeOfSourceFilesForCopy;//we got sizeOfSourceFilesForCopy in
        // setSizeOfSourceFilesForCopy(File file) when getting list of raw input files
    }


    //-------------------------------------- Get Info About File -------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private void getInfoOfFile(InputFile file) {

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getFile());

            for (Directory directory : metadata.getDirectories()) {

                for (Tag tag : directory.getTags()) {

                    //-------------------------  Getting file name. File name looks like fileName.jpg  -----------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("[File] File Name")) {
                        String[] testName = tag.toString().split("\\.");
                        if (testName.length != 2 || testName[1].equals("null")) {//Checking file name not looks like
                            // fileName.jpg.jpg
                            //TODO Fix it:
                            // This is allows doesn't copies strange files. But if file have
                            // several points in its name then files will not be copied.
                            break;
                        }
                        file.setName(tag.toString().substring(18).trim());
                    }

                    //-------------------------  Getting file Date in String  ------------------------------------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("File Modified Date")) {
                        String[] tempAllDates = tag.toString().substring(31).split(" ");
                        file.setTime(tempAllDates[2].trim());
                        file.setDay(tempAllDates[1].trim());
                        file.setMonth(tempAllDates[0].replace(".", ""));
                        file.setYear(tempAllDates[4].trim());
                    }

                    //-------------------------  Getting file Format in String  ----------------------------------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("Detected File Type Name")) {
                        file.setType(tag.toString().substring(38).trim());
                    }

                }
            }
        } catch (Exception empty) {
            // System.out.println("Exception in getInfoOfFile(File file) method");
        }
    }

    //-------------------------------------- Get Percent of Done -------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    public void getProcessOfDone() {
        setPercentOfDone((numberOfCopiedFiles * 100) / listOfSourceInputFilesForCopying.size());
    }


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


}

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


    //--------------------------------------------------- Getters and Setters ------------------------------------
    //------------------------------------------------------------------------------------------------------------


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
                    getInfoOfFile(file);//Add all information about Input File
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


    //--------------------------------------- Get List Of Input Files --------------------------------------------
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

    private void getInfoOfFile(InputFile inputFile) {

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(inputFile.getFile());

            for (Directory directory : metadata.getDirectories()) {

                for (Tag tag : directory.getTags()) {

                    //-------------------------  Getting inputFile name. File name looks like fileName.jpg  ------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("[File] File Name")) {
                        String[] testName = tag.toString().split("\\.");
                        if (testName.length != 2 || testName[1].equals("null")) {//Checking inputFile name not looks like
                            // fileName.jpg.jpg
                            //TODO Fix it:
                            // This is allows doesn't copies strange files. But if inputFile have
                            // several points in its name then files will not be copied.
                            break;
                        }
                        inputFile.setName(tag.toString().substring(18).trim());
                    }

                    //-------------------------  Getting inputFile Date in String  -------------------------------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("File Modified Date")) {
                        String[] tempAllDates = tag.toString().substring(31).split(" ");
                        inputFile.setTime(tempAllDates[2].trim());
                        inputFile.setDay(tempAllDates[1].trim());
                        inputFile.setMonth(tempAllDates[0].replace(".", ""));
                        inputFile.setYear(tempAllDates[4].trim());
                    }

                    //-------------------------  Getting inputFile Format in String  -----------------------------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("Detected File Type Name")) {
                        inputFile.setType(tag.toString().substring(38).trim());
                    }

                }
            }
        } catch (Exception empty) {
            // System.out.println("Exception in getInfoOfFile(File inputFile) method");
        }

    }


    //-------------------------------------- Copying File --------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private void copyingFile(InputFile inputFile) {

        if (!(inputFile.getName() == null)) {
            checkingAndCreatingDirectory(inputFile.getDestinationPathWithoutFileName()); //Checking and creating Folder for
            // Input File


            //TODO
            // inputFile.createDestinationPathWithFileName(); must be not here
            // Need trying do it | in getInfoOfFile(InputFile inputFile)
            //                  \/
            inputFile.createDestinationPathWithFileName();


            checkingForFilesWithDuplicateNames(inputFile);



        }
    }

    private void checkingAndCreatingDirectory(String destination) {
        File newFile = new File(destination);
        if (!newFile.exists()) {
            createNewDirectory(newFile);
        }
    }

    private void createNewDirectory(File file) {
        if (!file.mkdirs()) System.out.println("Directory didn't created");
    }




    private void checkingForFilesWithDuplicateNames(InputFile inputFile/*, String destinationPath*/) {


      String destinationPath = inputFile.getDestinationPathName();


        if (Files.exists(Paths.get(destinationPath))) {



            InputFile existFile = new InputFile(new File(destinationPath), destinationPath);
            getInfoOfFile(existFile);

            if (!existFile.equals(inputFile)) {


              String  newDestinationPath = createNewNameForRepeatingFile(destinationPath);

              inputFile.setDestinationPathName(newDestinationPath);

                checkingForFilesWithDuplicateNames(inputFile);

            }

        } else {

            writeFileToDestination(inputFile.getFile(),destinationPath);
        }
    }





    private void writeFileToDestination(File file, String destinationPath) {
        System.out.println(destinationPath);
        try {
            Files.copy(file.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            System.out.println("Writing file failed");
        }
    }


    private String createNewNameForRepeatingFile(String destinationPath) {
       // String destinationPath = inputFile.getDestinationPathWithFileName();
        String[] fileNameSplitByPoint = destinationPath.split("\\.");
        String[] fileNameSplitBySlash = fileNameSplitByPoint[0].split("\\\\");
        System.out.println(destinationPath);
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




    //-------------------------------------- Get Percent of Done -------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    public void getProcessOfDone() {
        setPercentOfDone((numberOfCopiedFiles * 100) / listOfSourceInputFilesForCopying.size());
    }








}

package Model;



import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class Model implements Runnable {
    private ArrayList<InputFile> listOfSourceInputFilesForCopying;
    private String sourcePathName;
    private static String destinationPathName;
    private boolean copyingContinues = true;
    private int numberOfCopiedFiles;
    private long sizeOfSourceFilesForCopy;
    private String messageToViewer;
    private int percentOfDone;
    private boolean copySelected = true;


    //--------------------------------------------------- Getters and Setters ------------------------------------
    //------------------------------------------------------------------------------------------------------------


    public boolean isCopySelected() {
        return copySelected;
    }

    public void setCopySelected(boolean copySelected) {
        this.copySelected = copySelected;
    }

    public static void setDestinationPathName(String destinationPathName) {
        Model.destinationPathName = destinationPathName;
    }

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

    public static String getDestinationPathName() {
        return destinationPathName;
    }


    //----------------------------------------------- Read and Write Settings ------------------------------------
    //------------------------------------------------------------------------------------------------------------

    public void readSettings() {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream("setting.txt"))) {
            sourcePathName = inputStream.readUTF();
            destinationPathName = inputStream.readUTF();
            setMessageToViewer("Ready to work");
        } catch (Exception e) {
            System.out.println("Exception in readSetting()");
            setMessageToViewer("Choose input and output Folders");
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
                    //This check need for deleting strange temporary files.
                } else if (rawInputFile.length() > 50000) {
                    System.out.println(rawInputFile.getAbsolutePath());
                    getListOfSourceInputFilesForCopying(new InputFile(rawInputFile));
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


    //-------------------------------------- Copying File --------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private void copyingFile(InputFile inputFile) {

        if (!(inputFile.getName() == null)) {

            checkingAndCreatingDirectory(inputFile.getAbsolutePathWithoutFileName()); //Checking and creating Folder for
            // Input File

            checkingForFilesWithDuplicateNames(inputFile);


            if (copySelected) {

                copyFileToDestination(inputFile);
            }else {
                moveFileToDestination(inputFile);
            }
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


    private void checkingForFilesWithDuplicateNames(InputFile inputFile) {

        String absolutePathName = inputFile.getAbsolutePathWithFileName();

        if (Files.exists(Paths.get(absolutePathName), LinkOption.NOFOLLOW_LINKS)) {

            InputFile existingFile = new InputFile(new File(absolutePathName));

            if (!existingFile.equals(inputFile)) {

                String newName = createNewNameForRepeatingFile(inputFile.getName());

                inputFile.setName(newName);

                inputFile.createAbsolutePathName();

                checkingForFilesWithDuplicateNames(inputFile);

            }


        }
    }


    private String createNewNameForRepeatingFile(String name) {
        String[] fileNameSplitByPoint = name.split("\\.");

        String newFileName;
        if (!fileNameSplitByPoint[fileNameSplitByPoint.length - 2].contains("Duplicate")) {

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


    private void copyFileToDestination(InputFile inputFile) {
        Path sourcePath = inputFile.getFile().toPath();
        Path destinationPath = Paths.get(inputFile.getAbsolutePathWithFileName());
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            System.out.println("Writing file failed");
        }
    }

    private void moveFileToDestination(InputFile inputFile) {
        Path sourcePath = inputFile.getFile().toPath();
        Path destinationPath = Paths.get(inputFile.getAbsolutePathWithFileName());
        try {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            System.out.println("Writing file failed");
        }
    }


    //-------------------------------------- Get Percent of Done -------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    public void getProcessOfDone() {
        setPercentOfDone((numberOfCopiedFiles * 100) / listOfSourceInputFilesForCopying.size());
    }


}

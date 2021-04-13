package Model;


import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Objects;

public class Model implements Runnable {

    private static String destinationPathName;
    private String sourcePathName;
    private boolean copyingContinues = true;
    private String messageToViewer;
    private int percentOfDone;
    private boolean copySelected = true;
    private long allFilesOfInputFolderSize;
    private long copiedFilesSize;
    private final ArrayList<PathPreset> listOfPresets = new ArrayList<>();


    //--------------------------------------------------- Getters and Setters ------------------------------------
    //------------------------------------------------------------------------------------------------------------


    public static String getDestinationPathName() {
        return destinationPathName;
    }

    public static void setDestinationPathName(String destinationPathName) {
        Model.destinationPathName = destinationPathName;
    }

    public long getCopiedFilesSize() {
        return copiedFilesSize;
    }

    public void setCopiedFilesSize(long copiedFilesSize) {
        this.copiedFilesSize = copiedFilesSize;
    }

    public boolean isCopySelected() {
        return copySelected;
    }

    public void setCopySelected(boolean copySelected) {
        this.copySelected = copySelected;
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

    public boolean isCopyingContinues() {
        return copyingContinues;
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

    public ArrayList<PathPreset> getListOfPresets() {
        return listOfPresets;
    }


    //----------------------------------------------- Read and Write Settings ------------------------------------
    //------------------------------------------------------------------------------------------------------------


    public void readSettings() {
        String name;
        String source;
        String destination;
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream("setting.txt"))) {

            listOfPresets.clear();

            while (inputStream.available() > 0) {
                name = inputStream.readUTF();
                source = inputStream.readUTF();
                destination = inputStream.readUTF();
                listOfPresets.add(new PathPreset(name, source, destination));
            }

            loadLastPreset();

            setMessageToViewer("Ready to work");

        } catch (Exception e) {
            System.out.println("Exception in readSetting()");
            name = "Default";
            source = "D:\\";
            destination = "D:\\";
            listOfPresets.add(new PathPreset(name, source, destination));
            writeSettings();
        }
    }

    public void loadLastPreset(){
        sourcePathName = listOfPresets.get(0).getSourcePathName();
        destinationPathName = listOfPresets.get(0).getDestinationPathName();
    }

    public void deletePreset(String presetName) {
        int index = 0;
        for (PathPreset preset : listOfPresets) {

            if (preset.getPresetName().equals(presetName)) {
               listOfPresets.remove(index);
                writeSettings();
                loadLastPreset();
               return;
            }
            index++;
        }
        writeSettings();
        loadLastPreset();
    }

    public void addNewPreset(String presetName) {
        listOfPresets.add(new PathPreset(presetName, sourcePathName, destinationPathName));
        writeSettings();
    }

    public void changeDefaultPreset() {
        listOfPresets.set(0, new PathPreset("Default", sourcePathName, destinationPathName));
        writeSettings();
    }


    public void loadPreset(String presetName) {

        for (PathPreset preset : listOfPresets) {
            if (preset.getPresetName().equals(presetName)) {
                sourcePathName = preset.getSourcePathName();
                destinationPathName = preset.getDestinationPathName();
            }
        }
       // changeDefaultPreset();
    }


    public void writeSettings() {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream("setting.txt"))) {
            for (PathPreset preset : listOfPresets) {
                outputStream.writeUTF(preset.getPresetName());
                outputStream.writeUTF(preset.getSourcePathName());
                outputStream.writeUTF(preset.getDestinationPathName());
            }
        } catch (Exception f) {
            System.out.println("Exception in writeSetting()");
        }
    }



    //---------------------------------------------- Start Method ------------------------------------------------
    //------------------------------------------------------------------------------------------------------------


    public void run() {

        Log.write("<<< Start " + sourcePathName + " >>>");
        setPercentOfDone(0);//If doesn't set zero then in some cases progress scale won't be showed.
        allFilesOfInputFolderSize = calculateSourceFolderSize(new File(sourcePathName));


        if (thereIsEnoughFreeSpaceForCopying() || !copySelected) {
            startProcess(sourcePathName);//We get input files.
        } else {
            setPercentOfDone(100);
            setMessageToViewer("Space is not Enough");
            return;
        }
        setPercentOfDone(100);
        setMessageToViewer("All Photo were copy");
        setCopiedFilesSize(0);

        Log.write("<<< End " + sourcePathName + " >>>\n\r\n\r\n\r");
    }


    public long calculateSourceFolderSize(File directory) {
        long length = 0;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile())
                length += file.length();
            else
                length += calculateSourceFolderSize(file);
        }
        return length;
    }


    //--------------------------------------- Get List Of Input Files --------------------------------------------
    //------------------------------------------------------------------------------------------------------------


    private void startProcess(String sourcePath) {

        File[] arrayOfIncomingFiles = getArrayOfInputFiles(sourcePath);

        if (arrayOfIncomingFiles.length != 0) {
            for (File file : arrayOfIncomingFiles) {

                setCopiedFilesSize(getCopiedFilesSize() + file.length());
                //  calculatePercentOfDone();

                if (!file.isFile() && isCopyingContinues()) {
                    startProcess(file.getAbsolutePath());
                    //This check need for deleting strange temporary files.


                } else if (/*file.length() > 50000 && !file.getAbsolutePath().contains("cof") &&*/isCopyingContinues()) {
                    startCopyingFile(new InputFile(file));
                    calculatePercentOfDone();
                }

            }

        }

    }

    private File[] getArrayOfInputFiles(String sourcePath) {
        File sourceFolder = new File(sourcePath);
        return sourceFolder.listFiles();
    }


    //-------------------------------------- Check Free space for copying ----------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private boolean thereIsEnoughFreeSpaceForCopying() {
        File destFolder = new File(destinationPathName);
        long destinationFreeSize = destFolder.getFreeSpace();

        return destinationFreeSize > allFilesOfInputFolderSize;//we got sizeOfSourceFilesForCopy in
        // setSizeOfSourceFilesForCopy(File file) when getting list of raw input files
    }


    //-------------------------------------- Copying File --------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private void startCopyingFile(InputFile inputFile) {

        if (!(inputFile.getName() == null)) {
            checkingAndCreatingDirectory(inputFile.getAbsolutePathOnlyFolders()); //Checking and creating Folder for
            // Input File

            checkingForFilesWithDuplicateNames(inputFile);


            if (copySelected) {
                copyFileToDestination(inputFile);
            } else {
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
            Log.write(inputFile.getAbsolutePathWithFileName() + " was not writing ");
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

    public void calculatePercentOfDone() {
        setPercentOfDone((int) ((getCopiedFilesSize() * 100) / allFilesOfInputFolderSize));
    }



}

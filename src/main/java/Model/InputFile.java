package Model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.util.Objects;

public class InputFile {
    private final File file;
    private String name;
    private String year;
    private String month;
    private String day;
    private String time;
    private String type;
    private String absolutePathWithFileName;
    private String absolutePathOnlyFolders;

    public InputFile(File file) {
        this.file = file;
        getInfoOfFile();
    }


    //--------------------------------------------------------- Getters and Setters ------------------------------
    //------------------------------------------------------------------------------------------------------------


    public String getAbsolutePathOnlyFolders() {
        return absolutePathOnlyFolders;
    }

    public String getAbsolutePathWithFileName() {
        return absolutePathWithFileName;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setYear(String year) {
        this.year = year;
    }


    public void setMonth(String month) {
        this.month = replaceMonthName(month);
    }


    public void setDay(String day) {
        this.day = day;
    }


    public void setTime(String time) {
        this.time = time;
    }


    public void setType(String type) {
        this.type = type;
    }


    //------------------------------------------ Method for getting info of input file ----------------------------
    //-------------------------------------------------------------------------------------------------------------


    private void getInfoOfFile() {
        try {

            Metadata metadata = null;

            //If length of file smaller than 50000 that its not a photo.
            if(this.getFile().length() < 50000)return;

            //Here checking existing large files like "TIF". When i tried read Metadata those files, i got OutOfMemory error. It issue in metadata extractor framework.
            //checking if this file is tif then we need  more checks.
            if (!this.getFile().getAbsolutePath().contains(".tif")) {
                metadata = ImageMetadataReader.readMetadata(this.getFile());
                //Checking tif file.
                //If length of file larger than 500000000 then metadata won't be able to read
            } else if (this.getFile().getAbsolutePath().contains(".tif") && this.getFile().length() < 500000000) {
                metadata = ImageMetadataReader.readMetadata(this.getFile());
                //If metadata didn't read then file path will be write to log file
            } else if (this.getFile().length() > 5000000) {
                Log.write(" I couldn't read the tif metadata " + this.getFile().getAbsolutePath() + "  " + (this.getFile().length())/1000000 +
                        " mb.");
            }


            assert metadata != null;
            for (Directory directory : metadata.getDirectories()) {

                for (Tag tag : directory.getTags()) {

                    //-------------------------  Getting inputFile name. File name looks like fileName.jpg  ------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("[File] File Name")) {
                            setName(tag.toString().substring(18).trim());
                    }


                    //-------------------------  Getting inputFile Date in String  -------------------------------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("File Modified Date")) {
                        String[] tempAllDates = tag.toString().substring(31).split(" ");
                        setTime(tempAllDates[2].trim());
                        setDay(tempAllDates[1].trim());
                        setMonth(tempAllDates[0].replace(".", ""));
                        setYear(tempAllDates[4].trim());
                    }

                    //-------------------------  Getting inputFile Format in String  -----------------------------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("Detected File Type Name")) {
                        setType(tag.toString().substring(38).trim());
                    }

                }

            }
            createAllNeededPaths(Model.getDestinationPathName());

        } catch (Exception empty) {
        // empty.printStackTrace();
        }


    }


    //------------------------------------------- Other methods ---------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------

    public void createAllNeededPaths(String destinationPath) {
        createNewAbsolutePathWithoutFileName(destinationPath);
        createNewAbsolutePathName();
    }


    public void createAbsolutePathName() {
        createNewAbsolutePathName();
    }


    private void createNewAbsolutePathName() {
        absolutePathWithFileName = absolutePathOnlyFolders + name;
    }

    private void createNewAbsolutePathWithoutFileName(String destinationPath) {

        //Creating one folder for all type of video.
        if (type.equals("MP4") || type.equals("MOV") || type.equals("M4V") || type.equals("3G2") || type.equals("3GP")) {
            type = "VIDEO";
        }

        absolutePathOnlyFolders = destinationPath + "\\" + type + "\\" + year + "\\" + month +
                " " + day +
                "\\";
    }

    private String replaceMonthName(String month) {
        switch (month) {
            case ("янв"):
                return "01 Январь";
            case ("февр"):
                return "02 Февраль";
            case ("мар"):
                return "03 Март";
            case ("апр"):
                return "04 Апрель";
            case ("мая"):
                return "05 Май";
            case ("июн"):
                return "06 Июнь";
            case ("июл"):
                return "07 Июль";
            case ("авг"):
                return "08 Август";
            case ("сент"):
                return "09 Сентябрь";
            case ("окт"):
                return "10 Октябрь";
            case ("нояб"):
                return "11 Ноябрь";
            case ("дек"):
                return "12 Декабрь";
        }
        return "Unknown";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputFile inputFile = (InputFile) o;
        return Objects.equals(year, inputFile.year) && Objects.equals(month, inputFile.month) && Objects.equals(day, inputFile.day) && Objects.equals(time, inputFile.time) && Objects.equals(type, inputFile.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, time, type);
    }
}

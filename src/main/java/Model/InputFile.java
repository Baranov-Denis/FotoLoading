package Model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFile {
    private final File file;
    private String name;
    private String year;
    private String month;
    private String day;
   // private String time;
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

    public String getYear() {
        return year;
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



    public void setType(String type) {
        this.type = type;
    }


    //------------------------------------------ Method for getting info of input file ----------------------------
    //-------------------------------------------------------------------------------------------------------------


    private void getInfoOfFile() {



        try {
            Metadata metadata;

            //If length of file smaller than 50000 that its not a photo. .cof is not photo but it will be copied.
            if (this.getFile().length() < 50000 || this.getFile().getName().contains(".cof")) return;

            //Here checking existing large files like "TIF". When i tried read Metadata those files, i got OutOfMemory error. It issue in metadata extractor framework.
            //If length of file larger than 500000000 then metadata won't be able to read
            if (this.getFile().length() < 500000000) {
                metadata = ImageMetadataReader.readMetadata(this.getFile());
                //If metadata didn't read then file path will be write to log file
            } else if (this.getFile().length() > 5000000 && !this.getFile().getAbsolutePath().contains(".tif")) {
                metadata = ImageMetadataReader.readMetadata(this.getFile());
            } else {
                createPathForUnsortedFiles();
                createAllNeededPaths(Model.getDestinationPathName());
                return;
            }


            assert metadata != null;
            for (Directory directory : metadata.getDirectories()) {

                for (Tag tag : directory.getTags()) {

                   // System.out.println(tag.toString());

                    //-------------------------  Getting inputFile name. File name looks like fileName.jpg  ------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("[File] File Name")) {
                        setName(tag.toString().substring(18).trim());
                    }


                    //-------------------------  Getting inputFile Date in String  -------------------------------
                    //--------------------------------------------------------------------------------------------

                    //"Date/Time Original" contains original date of when photo was created.
                    // If file doesn't contains "Date/Time Original" then will be used info from "File Modified Date".
                    // If "Date/Time Original" contains incorrect information then year will be set null and will be used info from "File Modified Date".
                    // File might contains several  "File Modified Date" and "Date/Time Original".

                    if(tag.toString().contains("Date/Time Original")){

                        String[] tempAllDates = tag.toString().substring(35,45).split(":");

                        Pattern pattern1 = Pattern.compile ("\\d{4}");
                        Matcher matcher = pattern1.matcher(tempAllDates[0].trim());

                            if(matcher.find()) {
                            setYear(tempAllDates[0].trim());
                        }else {
                            setYear(null);
                        }
                        setMonth(tempAllDates[1].trim());
                        setDay(tempAllDates[2].trim());

                    }else if(tag.toString().contains("File Modified Date") && this.getYear() == null) {
                        String[] tempAllDates = tag.toString().substring(31).split(" ");
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
            //All files will be placed to folder unsorted. But it might be not photos or videos. Size larger than 5 mb.
            if(this.getFile().length() > 5000000) {
                createPathForUnsortedFiles();
                createAllNeededPaths(Model.getDestinationPathName());
            }

        }


    }


    //------------------------------------------- Other methods ---------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------




    private void createPathForUnsortedFiles() {
        setName(this.getFile().getName());
        setType("Unsorted");
    }

    public void createAllNeededPaths(String destinationPath) {
        createNewAbsolutePathOnlyFolders(destinationPath);
        createNewAbsolutePathName();
    }


    public void createAbsolutePathName() {
        createNewAbsolutePathName();
    }


    private void createNewAbsolutePathName() {
        absolutePathWithFileName = absolutePathOnlyFolders + name;
    }

    private void createNewAbsolutePathOnlyFolders(String destinationPath) {
        //Creating one folder for all type of video.
        if (type.equals("MP4") || type.equals("MOV") || type.equals("M4V") || type.equals("3G2") || type.equals("3GP") || type.equals("AVI") || type.equals("VOB")) {
            type = "VIDEO";
        }
        if (type.equals("Unsorted")) {
            absolutePathOnlyFolders = destinationPath + "\\" + type + "\\";
        } else {
            absolutePathOnlyFolders = destinationPath + "\\" + type + "\\" + year + "\\" + month +
                    " " + day +
                    "\\";
        }
    }

    private String replaceMonthName(String month) {
        switch (month) {
            case ("01"):
            case ("янв"):
                return "01 Январь";
            case ("02"):
            case ("февр"):
                return "02 Февраль";
            case ("03"):
            case ("мар"):
                return "03 Март";
            case ("04"):
            case ("апр"):
                return "04 Апрель";
            case ("05"):
            case ("мая"):
                return "05 Май";
            case ("06"):
            case ("июн"):
                return "06 Июнь";
            case ("07"):
            case ("июл"):
                return "07 Июль";
            case ("08"):
            case ("авг"):
                return "08 Август";
            case ("09"):
            case ("сент"):
                return "09 Сентябрь";
            case ("10"):
            case ("окт"):
                return "10 Октябрь";
            case ("11"):
            case ("нояб"):
                return "11 Ноябрь";
            case ("12"):
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
        return Objects.equals(year, inputFile.year) && Objects.equals(month, inputFile.month) && Objects.equals(day,
                inputFile.day) && Objects.equals(this.getFile().length(), inputFile.getFile().length()) && Objects.equals(type,
                inputFile.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, this.getFile().length(), type);
    }
}

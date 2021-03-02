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
    private String absolutePathWithoutFileName;

    public InputFile(File file) {
        this.file = file;
        getInfoOfFile();
    }

    private void getInfoOfFile() {

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(this.getFile());

            for (Directory directory : metadata.getDirectories()) {

                for (Tag tag : directory.getTags()) {

                    //-------------------------  Getting inputFile name. File name looks like fileName.jpg  ------
                    //--------------------------------------------------------------------------------------------
                    if (tag.toString().contains("[File] File Name")) {
/*                        if (testName[1].equals("null")) {
                            break;
                        }*/
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
        } catch (Exception empty) {
            // System.out.println("Exception in getInfoOfFile(File inputFile) method");
        }

        createAllNeededPaths(Model.getDestinationPathName());

    }

    public String getAbsolutePathWithoutFileName() {
        return absolutePathWithoutFileName;
    }

    public String getAbsolutePathWithFileName() {
        return absolutePathWithFileName;
    }

    public void createAllNeededPaths(String destinationPath) {
        createNewAbsolutePathWithoutFileName(destinationPath);
        createNewAbsolutePathName(destinationPath);
    }


    public void createAbsolutePathName(String destinationPath) {
        createNewAbsolutePathName(destinationPath);
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



    private void createNewAbsolutePathName(String destinationPath) {
        absolutePathWithFileName = destinationPath + "\\" + type + "\\" + year + "\\" + year + " " + month +
                " " + day +
                "\\" + name;
    }

    private void createNewAbsolutePathWithoutFileName(String destinationPath) {
        absolutePathWithoutFileName = destinationPath + "\\" + type + "\\" + year + "\\" + year + " " + month +
                " " + day +
                "\\";
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

package Model;

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
    private final String destinationPathName;

    public String getDestinationPathWithFileName() {
        return createNewDestinationPathWithFileName();
    }

    public String getDestinationPathWithoutFileName() {
        return createNewDestinationPathWithoutFileName();
    }

    public InputFile(File file, String destinationPathName) {
        this.file = file;
        this.destinationPathName = destinationPathName;
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


    private String createNewDestinationPathWithoutFileName() {
        return destinationPathName + "\\" + type + "\\" + year + "\\" + year + " " + month +
                " " + day +
                "\\";
    }

    private String createNewDestinationPathWithFileName() {
        return destinationPathName + "\\" + type + "\\" + year + "\\" + year + " " + month +
                " " + day +
                "\\" + name;
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

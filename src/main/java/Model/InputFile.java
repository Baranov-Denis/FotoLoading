package Model;

import java.io.File;
import java.util.Objects;

public class InputFile {
    private File file;
    private String date;
    private String name;
    private String year;
    private String month;
    private String day;
    private String time;
    private String type;
    private String destinationPath;
    private String destinationPathWithoutFileName;
    private String destinationPathWithFileName;

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getDestinationPathWithFileName() {
        return createNewDestinationPathWithFileName();
    }

    public void setDestinationPathWithFileName(String destinationPathWithFileName) {
        this.destinationPathWithFileName = destinationPathWithFileName;
    }

    public String getDestinationPathWithoutFileName() {
        return createNewDestinationPathWithoutFileName();
    }

    public void setDestinationPathWithoutFileName(String destinationPathWithoutFileName) {
        this.destinationPathWithoutFileName = destinationPathWithoutFileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public InputFile(File file, String destinationPath) {
        this.file = file;
        this.destinationPath = destinationPath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public InputFile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = replaceMonthName(month);
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    private String createNewDestinationPathWithoutFileName() {
        return destinationPath + "\\" + type + "\\" + year + "\\" + year + " " + month +
                " " + day +
                "\\";
    }

    private String createNewDestinationPathWithFileName() {
        return destinationPath + "\\" + type + "\\" + year + "\\" + year + " " + month +
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

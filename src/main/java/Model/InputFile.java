package Model;

public class InputFile {
    private String name;
    private String year;
    private String month;
    private String day;
    private String time;
    private String type;

    public InputFile(String name, String year, String month, String day, String time, String type) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.time = time;
        this.type = type;
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
        this.month = month;
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
}

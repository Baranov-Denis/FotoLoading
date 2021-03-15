package Model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static void write(String log){
        try(FileWriter fileWriter = new FileWriter("wrongFiles.log" , true)){
            fileWriter.write(getCurrentDate() + " ---- " + log + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy ' at ' HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}

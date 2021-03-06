package Model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static void write(String log){
        try(FileWriter outputStream = new FileWriter("log.txt" , true)){
            outputStream.write(getDate() + " ---- " + log + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String getDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy ' at ' HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}

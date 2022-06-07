package os.hw2.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

public class Logger {
    private static Logger instance;
    private PrintWriter pw;

    public static String processName;

    public static boolean isDebug = true;

    public static Logger getInstance(){
        if(instance == null)
            return instance = new Logger();
        return instance;
    }

    private void createFile(){
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter("C:\\Users\\Alico\\Desktop\\logs.txt", true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Logger(){
        createFile();
    }

    public void log(String message){
        if(isDebug) {
            pw.println(processName + ":  " + message + "   @   " + new Timestamp(System.currentTimeMillis()));
            pw.flush();
        }
    }
}

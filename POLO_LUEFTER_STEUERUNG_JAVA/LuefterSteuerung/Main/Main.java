package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.*;
import com.fazecast.jSerialComm.*;

public class Main {
    private static final String VERSION = "1.0.0.2";
    public static boolean debug = false;
    public static boolean logging = false;
    public static boolean log_ready = false;
    public static String branch = "master";
    public static String projectUri = "https://raw.githubusercontent.com/lukasaldersley/POLO_LUEFTER_STEUERUNG/";
    public static String downloadTargetUri = "https://github.com/lukasaldersley/POLO_LUEFTER_STEUERUNG/raw/";
    private static boolean fromUpdate;
    private static FileWriter logWriter;
    private static File logFile;
    private static File oldFile;
    private static URL source;
    private static ReadableByteChannel readableByteChannelFromSource;
    private static FileOutputStream fileOutputStream;
    private static BufferedReader br;
    private static Scanner serialScanner;
    private static BufferedWriter serialWriter;
	private static String PORTNAME="TODO";
	private static JFrame frame;

    
    /**
     * Checks whether an Update is available
     * 
     * @return true if an update is available, false if no update is available
     */
    public static boolean checkIfUpdateAvailable() {
        try {
            source = new URL(projectUri + branch + "/ready/VERSION");

            logln(projectUri + branch + "/ready/VERSION");
            br = new BufferedReader(new InputStreamReader(source.openStream()));
            String RemoteVersion = br.readLine().trim();
            // in String.split muss ein '.' escaped werden
            // http://www.java-examples.com/java-string-split-example
            // 06.09.2017 3:46 Uhr
            // (codezeilen 31-41 im beispiel auf der website)
            String[] remote = RemoteVersion.split("\\.");
            String[] local = VERSION.split("\\.");
            for (int i = 0; i < 4; i++) {

                logln(local[i] + " | " + remote[i]);
                if (Integer.parseInt(remote[i]) > Integer.parseInt(local[i])) {
                    return true;
                } else if (Integer.parseInt(local[i]) == Integer.parseInt(remote[i])) {
                    ;
                } else {
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * After the Programm has checked whether an update from github is available it
     * performs this update
     */
    public static void updateJar() {
        logln("\n\nUPDATING\n\n");
        try {
            // quelle wie oben bei der config datei
            oldFile = new File("PoloLuefterSteuerung.jar");
            oldFile.renameTo(new File("PoloLuefterSteuerung_old.jar"));
            // oldFile.delete();
            // fileUrl:
            // "https://raw.githubusercontent.com/lukasaldersley/Raumklima/master/PublicVersion/VERSION"
            source = new URL(downloadTargetUri + branch + "/ready/PoloLuefterSteuerung.jar");
            readableByteChannelFromSource = Channels.newChannel(source.openStream());
            fileOutputStream = new FileOutputStream("PoloLuefterSteuerung.jar");
            fileOutputStream.getChannel().transferFrom(readableByteChannelFromSource, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            String parameters = "";
            if (debug) {
                parameters += " -d";
            }
            if (logging) {
                parameters += " -l";
            }
            oldFile = new File("PoloLuefterSteuerung_old.jar");
            oldFile.delete();
            Runtime.getRuntime().exec("java -jar PoloLuefterSteuerung.jar" + parameters);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            logln(e);
            oldFile = new File("PoloLuefterSteuerung.jar");
            if (!oldFile.exists()) {
                oldFile = new File("PoloLuefterSteuerung_old.jar");
                oldFile.renameTo(new File("PoloLuefterSteuerung.jar"));
            }
        }
    }

    public static void log(Object msg) {// anstatt von System.out.print() wird log() verwendet; folgende methoden analog
        if (debug) {
            System.out.print(msg);
        }
        try {
            if (logging) {
                if (log_ready) {// falls logdatei schon vorhanden => reinschreiben
                    logWriter.write(String.valueOf(msg));
                    logWriter.flush();
                } else {// sonst erst ertellen und dann reinschreiben
                    logFile = new File(
                        "PoloLuefterSteuerungLog_" + new SimpleDateFormat("dd.MM.yyyy_HH,mm").format(new Date()) + ".txt");
                    logWriter = new FileWriter(logFile);
                    log_ready = true;
                    logWriter.write(String.valueOf(msg));
                    logWriter.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logln(Object msg) {
        if (debug) {
            System.out.println(msg);
        }
        try {
            if (logging) {
                if (log_ready) {
                    logWriter.write(String.valueOf(msg) + System.lineSeparator());
                    logWriter.flush();
                } else {
                    logFile = new File(
                        "RaumklimaLog_" + new SimpleDateFormat("dd.MM.yyyy_HH,mm").format(new Date()) + ".txt");
                    logWriter = new FileWriter(logFile);
                    log_ready = true;
                    logWriter.write(String.valueOf(msg) + System.lineSeparator());
                    logWriter.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logln() {
        if (debug) {
            System.out.println();
        }
        try {
            if (logging) {
                if (log_ready) {
                    logWriter.write(System.lineSeparator());
                    logWriter.flush();
                } else {
                    logFile = new File(
                        "RaumklimaLog_" + new SimpleDateFormat("dd.MM.yyyy_HH,mm").format(new Date()) + ".txt");
                    logWriter = new FileWriter(logFile);
                    log_ready = true;
                    logWriter.write(System.lineSeparator());
                    logWriter.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int factorial(int in){//irgendwie f�r die cmd arguments
        int res=1;
        for(int i=in;i>0;i--){
            res*=i;
        }
        return res;
    }

    public static String permutation(String str) { //irgendwie f�r die cmd arguments
        String x= permutation("", str,"");
        return x.substring(0,x.length()-1);
    }

    private static String permutation(String prefix, String str,String returnValue) {//irgendwie f�r die cmd arguments
        int n = str.length();
        if (n == 0) {
            System.out.println(prefix);
            returnValue+=prefix+"|";
        }
        else {
            for (int i = 0; i < n; i++)
                returnValue=permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n),returnValue);
        }
        return returnValue;
    }

    public static boolean testPerm(String testAgainst,String permutator) {//irgendwie f�r die cmd arguments
        System.out.println(factorial(permutator.length()));
        String[] testCases=permutation(permutator).split("|");
        for(String T:testCases){
            if(testAgainst.equalsIgnoreCase(T)||testAgainst.equalsIgnoreCase("/"+T)||testAgainst.equalsIgnoreCase("-"+T)){
                return true;
            }
        }
        return false;
    }

    public static boolean tp(String args,String t){//irgendwie f�r die cmd arguments
        return (t.length()==1)?eq(args,t):testPerm(args,t);
    }

    public static boolean eq(String a,String b){//irgendwie f�r die cmd arguments
        return (a.equalsIgnoreCase(b)||a.equalsIgnoreCase("-"+b)||a.equalsIgnoreCase("/"+b)||a.equalsIgnoreCase("--"+b));
    }

    // TODO add log for UpdateProcedure
    public static void main(String[] args) {// Startet das Programm (ggf mnit debug/logging)
        for (int i = 0; i < args.length; i++) {
            if (eq(args[i],"log") || tp(args[i],"l")) {
                logging = true;
                log("");
                System.out.println("Dateiname f�r die Aufzeichnung: " + logFile.getName());
            }
            else if (eq(args[i],"debug") || tp(args[i],"d")) {
                debug = true;
                logln("Debugmodus aktiviert");
            }
            else if (tp(args[i],"u")) {
                fromUpdate = true;
            }
            else if (tp(args[i],"du")) {
                fromUpdate = true;
                debug = true;
                logln("Debugmodus aktiviert");
            }
            else if (tp(args[i],"lu")) {
                fromUpdate = true;
                logging = true;
                log("");
                System.out.println("Dateiname f�r die Aufzeichnung: " + logFile.getName());
            }
            else if (tp(args[i],"dlu")) {
                debug = true;
                logln("Debugmodus aktiviert");
                logging = true;
                log("");
                System.out.println("Dateiname f�r die Aufzeichnung: " + logFile.getName());
            }
            else if (tp(args[i],"dl")) {
                debug = true;
                logln("Debugmodus aktiviert");
                logging = true;
                log("");
                System.out.println("Dateiname f�r die Aufzeichnung: " + logFile.getName());
            }
            else if (tp(args[i],"?") || eq(args[i],"help") || tp(args[i],"h")) {
                System.out.println(
                    "\nBefehlszeilenparameter\n\n\"d\" oder \"debug\"\t\tDebugmodus\n\"l\" oder \"log\"\t\t\tAusgabe in Datei abspeichern\n"
                    + "\tSie d�rden anstatt -dl nat�rlich auch -d -l schreiben.\n");
                System.exit(0);
            }
        }

        logln(Charset.defaultCharset());
        // Check for Software updates
        if (!fromUpdate) {
            if (checkIfUpdateAvailable()) {
                updateJar();
            }
        }

        SerialPort[] ports=SerialPort.getCommPorts();
        for(SerialPort port:ports) {
            String name=port.getDescriptivePortName();
            if(name.contains("Bluetooth")){
                logln("skipped bluetooth serial port");
                continue;
            }
            if(name.equals(PORTNAME)){
                logln(name);
                //delete again this is just for structure
                port.setBaudRate(115200);
                boolean bool=true;
                bool=port.openPort();
                bool=port.isOpen();
                serialScanner = new Scanner(new InputStreamReader(port.getInputStream()));
                serialWriter = new BufferedWriter(new OutputStreamWriter(port.getOutputStream()));
            }
        }
        frame=new JFrame("TEST");
        frame.add(new JLabel("HALLO, Version 1.0.0.2"));
        frame.setBounds(500, 500, 200, 200);
        frame.setVisible(true);
        
    }
}

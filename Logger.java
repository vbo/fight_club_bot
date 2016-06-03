package ChatBot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.LinkedList;
import java.util.Date;
import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Logger {
  private static String logsFile;
  private static String clientsPath;
  private static String varsPath;
  private static final String EXT = ".db";
  private static PrintWriter logsWriter;

  static void setDbPath(String path) {
    logsFile = path + "/network";
    clientsPath = path + "/clients/";
    varsPath = path + "/vars/";
  }

  static List<String> getAllClientNames() {
    List<String> result = new LinkedList<>();
    File folder = new File(clientsPath);
    File[] listOfFiles = folder.listFiles();
    for (File f : listOfFiles) {
      if (f.isFile()) {
        String fileName = f.getName();
        String name = fileName.substring(0, fileName.length() - EXT.length());
        result.add(name);
      }
    }
    return result;
  }

  static void saveClient(String name, String value) {
    try {
      FileWriter fw = new FileWriter(clientsPath + name + EXT, false);
      fw.write(value);
      fw.close();
    } catch (IOException e) {
      System.out.println("Can't save client " + e);
      assert false;
    }
  }

  static String getClient(String name) {
    String value = null;
    try {
      FileReader fr = new FileReader(clientsPath + name + EXT);
      BufferedReader br = new BufferedReader(fr);
      value = br.readLine();
      br.close();
    } catch (Exception e) {}
    return value;
  }

  static void saveIntVar(String name, int value) {
    try {
      // TODO: for some variables (maxUpdateId) it would be better to open file once
      // and use flush to save data.
      FileWriter fw = new FileWriter(varsPath + name + EXT, false);
      fw.write(Integer.toString(value));
      fw.close();
    } catch (IOException e) {
      System.out.println("Can't save variable " + e);
      assert false;
    }
  }

  static Integer getIntVar(String name) {
    String value = null;
    try {
      FileReader fr = new FileReader(varsPath + name + EXT);
      BufferedReader br = new BufferedReader(fr);
      value = br.readLine();
      br.close();
    } catch (Exception e) {}
    if (value == null || value == "") {
      return null;
    }
    return Integer.parseInt(value);
  }

  static void log(String entry) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    entry = dateFormat.format(date) + " " + entry;
    try {
      Logger.getLogsWriter().println(entry);
    } catch (Exception e) {
      System.out.println("can't write logs " + e);
      System.exit(3);
    }
  }

  private static PrintWriter getLogsWriter() throws IOException {
    if (logsWriter == null) {
      FileWriter fw = new FileWriter(logsFile + EXT, true);
      BufferedWriter bw = new BufferedWriter(fw);
      logsWriter = new PrintWriter(bw, true);
    }
    return logsWriter;
  }
}

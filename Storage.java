package ChatBot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Storage {
  private static final String LOGS_FILE = "network.log.txt";
  private static PrintWriter logsWriter;

  public static void log(String entry) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    entry = dateFormat.format(date) + " " + entry;
    try {
      Storage.getLogsWriter().println(entry);
    } catch (Exception e) {
      System.out.println("can't write logs " + e);
      System.exit(3);
    }
  }

  private static PrintWriter getLogsWriter() throws IOException {
    if (logsWriter == null) {
      FileWriter fw = new FileWriter(LOGS_FILE, true);
      BufferedWriter bw = new BufferedWriter(fw);
      logsWriter = new PrintWriter(bw, true);
    }
    return logsWriter;
  }
}

package ChatBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Utils {
  static String getMatch(String str, Pattern p) {
    Matcher m = p.matcher(str);
    m.find();
    return m.group(1);
  }

  static int rndInRange(int min, int max) {
    int range = (max - min) + 1;
    return (int)(Math.random() * range) + min;
  }

  static <K> K getRnd(K[] arr) {
    return arr[rndInRange(0, arr.length - 1)];
  }
}

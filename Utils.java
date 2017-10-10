package ChatBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  static int rndInRangeWeighted(int[] arr) {
    int sum = 0;
    for (int el : arr) {
      sum += el;
    }
    int rnd = rndInRange(1, sum);
    for (int i = 0; i < arr.length; i++) {
      rnd = rnd - arr[i];
      if (rnd <= 0) {
        return i;
      }
    }
    assert false;
    return -1;
  }

  static <K> K getRnd(K[] arr) {
    assert arr.length > 0;
    return arr[rndInRange(0, arr.length - 1)];
  }

  static <K> K getRnd(Map<K, Integer> map) {
    Set<K> keyset = map.keySet();
    List<K> list = new ArrayList<K>();
    for (K key : keyset) {
      for (int i = 0; i < map.get(key); i++) {
        list.add(key);
      }
    }
    return list.get(rndInRange(0, list.size() -1));
  }
}


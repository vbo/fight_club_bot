package ChatBot;

class Utils {
  static int rndInRange(int min, int max) {
    int range = (max - min) + 1;
    return (int)(Math.random() * range) + min;
  }

  static <K> K getRnd(K[] arr) {
    return arr[rndInRange(0, arr.length - 1)];
  }
}

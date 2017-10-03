package ChatBot;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.BufferedReader;

class Phrases {

  static String[] wasDoingSomething;
  static String[] when;
  static String[] adjective;
  static String[] wasTrying;
  static String[] but;
  static String[] blocked;
  static String[] missed;
  static String[] said;
  static String[] wisdomIntro;
  static String[] wisdoms;
  static String[] won;
  static String[] lookingForOpponent;

  static String[][] hit;
  static String[][] toHit;

  static String getLang() {
        return "en";
  };

  private static String readJsonFile(String filename) {
    String jsonStr = "";
    try {
      FileReader fr = new FileReader(filename);
      BufferedReader br = new BufferedReader(fr);
      String line = "";
      while ((line = br.readLine()) != null) {
          jsonStr += line;
      }
    } catch (Exception e) {
      System.out.println(e);
    }

    return jsonStr;
  }

  public static void initialize() {
    JsonParser parser = new JsonParser();
    Gson g = new Gson();
    String lang = getLang();

    String jsonStr;
    try {
      jsonStr = readJsonFile("./text/wasDoingSomething.json");
      wasDoingSomething = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/when.json");
      when = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/adjective.json");
      adjective = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/wasTrying.json");
      wasTrying = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/but.json");
      but = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/blocked.json");
      blocked = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/missed.json");
      missed = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/said.json");
      said = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/wisdomIntro.json");
      wisdomIntro = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/wisdoms.json");
      wisdoms = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/won.json");
      won = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/lookingForOpponent.json");
      lookingForOpponent = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = readJsonFile("./text/hit.json");
      hit = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[][].class);

      jsonStr = readJsonFile("./text/toHit.json");
      toHit = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[][].class);

    } catch (Exception e) {
      System.out.println(e);
    }
  }
}

package ChatBot;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileReader;

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
  // TODO(Riboloff): ask the user which language they prefer. Move this to appropriate class.
        return "en";
  };


  public static void initialize() {
    JsonParser parser = new JsonParser();
    Gson g = new Gson();
    String lang = getLang();
    String jsonStr;

    try {
      jsonStr = Logger.readAllFile("./text/wasDoingSomething.json");
      wasDoingSomething = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/when.json");
      when = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/adjective.json");
      adjective = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/wasTrying.json");
      wasTrying = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/but.json");
      but = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/blocked.json");
      blocked = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/missed.json");
      missed = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/said.json");
      said = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/wisdomIntro.json");
      wisdomIntro = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/wisdoms.json");
      wisdoms = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/won.json");
      won = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/lookingForOpponent.json");
      lookingForOpponent = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[].class);

      jsonStr = Logger.readAllFile("./text/hit.json");
      hit = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[][].class);

      jsonStr = Logger.readAllFile("./text/toHit.json");
      toHit = g.fromJson(parser.parse(jsonStr).getAsJsonObject().getAsJsonArray(lang), String[][].class);

    } catch (Exception e) {
      Logger.logException(e);
    }
  }
}

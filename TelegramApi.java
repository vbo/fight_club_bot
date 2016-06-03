package ChatBot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;

class TelegramApi {
  private static final String TOKEN = "224259678:AAFl8SSyGhq3gwp99x1YNu5XdCbyIjgP3ns";
  private static final String TOKEN_DEV = "202943285:AAGPfX6FqP2GSjR3BRCZd3BD0eDKjeL_CF4";
  private static final String URL = "https://api.telegram.org/bot";
  private final String method;
  private final String params;
  HttpURLConnection connection;
  private static Gson g = new Gson();

  public static void say(int chatId, String text, String[] buttonTexts) {
    String params = "chat_id=" + chatId + "&text=" + text
      + "&parse_mode=Markdown";
    if (buttonTexts.length > 0) {
      int numberOfRows = (int)Math.ceil(buttonTexts.length/3.0);
      Telegram.Button[][] arr = new Telegram.Button[numberOfRows][];
      int row = 0;
      int col = 0;
      for (int i = 0; i < buttonTexts.length; i++) {
        if (col == 0) {
          arr[row] = new Telegram.Button[Math.min(3, buttonTexts.length - i)];
        }
        arr[row][col] = new Telegram.Button(buttonTexts[i]);
        col++;
        if (col == 3) {
          row++;
          col = 0;
        }
      }
      params += "&reply_markup={\"keyboard\":" + g.toJson(arr) + "}";
    }
    TelegramApi req = new TelegramApi("sendMessage", params);
    req.execute();
  }

  public static void say(int chatId, String text) {
    String[] replies = {};
    say(chatId, text, replies);
  }

  public static Telegram.Update[] getUpdates(int offset) {
    TelegramApi req = new TelegramApi(
      "getUpdates",
      "offset=" + offset);
    String resp = req.execute();
    if (resp == "") {
      return new Telegram.Update[0];
    }
    Telegram.GetUpdatesResult updates =
        g.fromJson(resp, Telegram.GetUpdatesResult.class);
    return updates.result;
  }

  private TelegramApi(String method) {
    this(method, "");
  }

  private TelegramApi(String method, String params) {
    this.method = method;
    this.params = params;
    Logger.log("request: " + method + "?" + params);
  }

  public String execute() {
    String result = "";
    try {
      connection = getConnection();
      sendRequest();
      result = getResponse();
    } catch (Exception e) {
      System.out.println("Error while handling http request " + e);
    }
    Logger.log("response: " + result);
    return result;
  }

  private String getResponse() throws IOException {
    InputStream is = connection.getInputStream();
    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    StringBuilder response = new StringBuilder();
    String line;
    while((line = rd.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }
    rd.close();
    return response.toString();
  }

  private void sendRequest() throws IOException {
    DataOutputStream wr = new DataOutputStream(
        connection.getOutputStream());
    wr.writeBytes(params);
    wr.close();
  }

  private HttpURLConnection getConnection()
      throws MalformedURLException, IOException, ProtocolException {
    URL url = new URL(URL + TOKEN + "/" + method);
    connection = (HttpURLConnection)url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type",
        "application/x-www-form-urlencoded");
    connection.setRequestProperty("Content-Length",
        Integer.toString(params.getBytes().length));
    connection.setRequestProperty("Content-Language", "en-US");
    connection.setUseCaches(false);
    connection.setDoOutput(true);
    return connection;
  }
}

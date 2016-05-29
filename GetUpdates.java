package ChatBot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;

import java.lang.reflect.Type;

class GetUpdates {
  public static void main(String[] args) {
    Telegram.Update[] updates = getUpdates();
    for (Telegram.Update upd : updates) {
      int chatId = upd.message.chat.id;
      sayHi(chatId);
    }
  }

  private static void sayHi(int chatId) {
    HttpRequest req = new HttpRequest(
      "sendMessage", 
      "chat_id="+chatId+"&text=hi");
    req.execute();
  }

  private static Telegram.Update[] getUpdates() {
    HttpRequest req = new HttpRequest("getUpdates", "");
    String resp = req.execute();
    Gson g = new Gson();
    Telegram.GetUpdatesResult updates =
        g.fromJson(resp, Telegram.GetUpdatesResult.class);
    return updates.result;
  }
}

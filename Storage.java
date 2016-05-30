package ChatBot;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

class Storage {
  private static Map<Integer, String> clients = new HashMap<>();
  private static Gson g = new Gson();
  private static int maxUpdateId = 0;

  static Client getClientByChatId(int chatId) {
    String clientJson = clients.get(chatId);
    if (clientJson == null) {
      return null;
    }
    return g.fromJson(clientJson, Client.class);
  }

  static void saveClient(int chatId, Client client) {
    clients.put(chatId, g.toJson(client)); 
  }
 
  static int getMaxUpdateId() {
    return maxUpdateId;
  }

  static void saveMaxUpdateId(int id) {
    maxUpdateId = id;
  }
}

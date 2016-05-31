package ChatBot;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Storage {
  private static Map<Integer, String> clients = new HashMap<>();
  private static Gson g = new Gson();
  private static int maxUpdateId = 0;

  static List<Client> getOpponentsReadyToRestore() {
    long curTime = System.currentTimeMillis() / 1000L;
    List<Client> result = new LinkedList<Client>();
    for (String clientJson : clients.values()) {
      Client c = g.fromJson(clientJson, Client.class);
      if (c.status == Client.Status.IDLE 
          && c.hp < c.maxHp
          && c.lastRestore <= curTime - 1000) {
        result.add(c);
      }
    }
    return result;
  }

  static Client getOpponentReadyToFight() {
    for (String clientJson : clients.values()) {
      Client c = g.fromJson(clientJson, Client.class);
      if (c.status == Client.Status.READY_TO_FIGHT) {
        return c;
      }
    }
    return null;
  }

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

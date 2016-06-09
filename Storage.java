package ChatBot;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Storage {
  private static Map<Integer, String> clients = new HashMap<>();
  private static Gson g = new Gson();

  static void forEachClient(ClientDo doable) {
    List<String> chatIds = Logger.getAllClientNames();
    for (String chatId : chatIds) {
      String clientJson = Logger.getClient(chatId);
      Client c = g.fromJson(clientJson, Client.class);
      if (c == null) {
        Logger.logException(new Exception(clientJson + " - " + chatId));
        continue;
      }
      doable.run(c);
    }
  }

  static Client getOpponentReadyToFight() {
    List<String> chatIds = Logger.getAllClientNames();
    for (String chatId : chatIds) {
      String clientJson = Logger.getClient(chatId);
      Client c = g.fromJson(clientJson, Client.class);
      if (c == null) {
        Logger.logException(new Exception(clientJson + " - " + chatId));
        continue;
      }
      if (c.status == Client.Status.READY_TO_FIGHT) {
        return c;
      }
    }
    return null;
  }

  static Client getClientByChatId(int chatId) {
    String clientJson = Logger.getClient(Integer.toString(chatId));
    if (clientJson == null) {
      return null;
    }
    return g.fromJson(clientJson, Client.class);
  }

  static void saveClient(Client client) {
    Logger.saveClient(Integer.toString(client.chatId), g.toJson(client));
  }

  static int getMaxUpdateId() {
    Integer result = Logger.getIntVar("maxUpdateId");
    if (result == null) {
      result = 0;
    }
    return result;
  }

  static void saveMaxUpdateId(int id) {
    Logger.saveIntVar("maxUpdateId", id);
  }
}

interface ClientDo {
  public void run(Client c);
}

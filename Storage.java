package ChatBot;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Storage {
  private static Map<Integer, String> clients = new HashMap<>();
  private static Gson g = new Gson();

  static List<Client> getClientsWaitingLongToFight() {
    long curTime = System.currentTimeMillis() / 1000L;
    List<Client> result = new LinkedList<>();
    List<String> chatIds = Logger.getAllClientNames();
    for (String chatId : chatIds) {
      String clientJson = Logger.getClient(chatId);
      Client c = g.fromJson(clientJson, Client.class);
      if (c.status == Client.Status.READY_TO_FIGHT
          && c.readyToFightSince <= curTime - 10) {
        result.add(c);
      }
    }
    return result;
  }

  static List<Client> getClientsReadyToRestore() {
    long curTime = System.currentTimeMillis() / 1000L;
    List<Client> result = new LinkedList<>();
    List<String> chatIds = Logger.getAllClientNames();
    for (String chatId : chatIds) {
      String clientJson = Logger.getClient(chatId);
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
    List<String> chatIds = Logger.getAllClientNames();
    for (String chatId : chatIds) {
      String clientJson = Logger.getClient(chatId);
      Client c = g.fromJson(clientJson, Client.class);
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

  static void saveClient(int chatId, Client client) {
    Logger.saveClient(Integer.toString(chatId), g.toJson(client));
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

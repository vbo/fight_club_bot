package ChatBot;

import java.lang.InterruptedException;
import java.lang.Thread;

import java.util.Arrays;
import java.util.Comparator;

public class Main {


  public static void main(String[] args)
      throws InterruptedException {
    long startTime = System.currentTimeMillis() / 1000L;
    System.out.println("Initialising main loop " + startTime);
    int maxUpdateId = Storage.getMaxUpdateId();
    while (true) {
      Telegram.Update[] updates = TelegramApi.getUpdates(maxUpdateId + 1);
      Arrays.sort(updates, new Comparator<Telegram.Update>() {
        public int compare(Telegram.Update u1, Telegram.Update u2) {
          return u1.update_id - u2.update_id;
        }
      });
      for (Telegram.Update upd : updates) {
        if (upd.message.date > startTime) {
          int chatId = upd.message.chat.id;
          Client client = Storage.getClientByChatId(chatId);
          if (client == null) {
            client = new Client(chatId);
          }
          String txt = upd.message.text;
          if (txt.equals("hi")) {
            TelegramApi.say(client.chatId, "hi!");
          } else if (txt.equals("stats")) {
            TelegramApi.say(client.chatId, "Your hp is " + client.hp);
          } else if (txt.equals("potion")) {
            client.hp += 10; 
            TelegramApi.say(client.chatId, "Potion consumed");
          } else {
            TelegramApi.say(client.chatId, "No such command");
          }
          maxUpdateId = upd.update_id;
          Storage.saveMaxUpdateId(maxUpdateId);
          // TODO: dragons here, updateId is written, client is not
          Storage.saveClient(chatId, client);
        }
      }
      Thread.sleep(2000); // 10s
    }
  }
}

class Client {
  int chatId = 0;
  int hp = 45;
  Client(int chatId) {
    this.chatId = chatId;
  }
}


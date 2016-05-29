package ChatBot;

import java.lang.InterruptedException;
import java.lang.Thread;

public class Main {

  public static void main(String[] args)
      throws InterruptedException {
    long unixTime = System.currentTimeMillis() / 1000L;
    System.out.println("Initialising main loop " + unixTime);
    int maxUpdateId = 0; 
    while (true) {
      Telegram.Update[] updates = TelegramApi.getUpdates(maxUpdateId + 1);
      for (Telegram.Update upd : updates) {
        if (upd.update_id > maxUpdateId) {
          maxUpdateId = upd.update_id;
        }
        if (upd.message.date > unixTime) {
          int chatId = upd.message.chat.id;
          TelegramApi.say(chatId, upd.message.text);
        }
      }
      Thread.sleep(10000); // 10s
    }
  }
}

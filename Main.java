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
          } else if (txt.equals("fight")) {
            if (client.status != Client.Status.IDLE) {
              TelegramApi.say(client.chatId, "You're not idle");
            } else {
              //TODO: status should be ready to fight
              client.status = Client.Status.FIGHTING;
              Client bot = new Client(-client.chatId);
              client.fightingChatId = bot.chatId;
              bot.fightingChatId = client.chatId;
              bot.status = Client.Status.FIGHTING;
              Storage.saveClient(bot.chatId, bot);
              TelegramApi.say(
                client.chatId, "You're now fighting with a bot");
            }
          } else if (txt.equals("hit")) {
            if (client.status != Client.Status.FIGHTING) {
              TelegramApi.say(
                client.chatId, "You need to start a fight first");
            } else {
              Client opponent = Storage.getClientByChatId(client.fightingChatId);
              assert opponent != null;
              if (opponent.chatId < 0) {
                int botHits = randomWithRange(0, 10);
                TelegramApi.say(
                  client.chatId, "Bot hits you by " + botHits + " hp.");
                int clientHits = randomWithRange(0, 10);
                TelegramApi.say(
                  client.chatId, "You hit a bot with " + clientHits + " hp.");
                opponent.hp -= clientHits;
                client.hp -= botHits;
                boolean fightFinished = false;
                if (client.hp <= 0) {
                  TelegramApi.say(
                    client.chatId, "You died.");
                  fightFinished = true;
                }
                if (opponent.hp <= 0) {
                  TelegramApi.say(
                    client.chatId, "Bot is dead. Congrats!");
                  fightFinished = true;
                }
                if (fightFinished) {
                  client.status = Client.Status.IDLE;
                  TelegramApi.say(
                    client.chatId, "Fight is finished.");
                  // TODO: destroy bot
                }
              } else {
                // TODO: real opponent should be here
                assert false;
              }
            }
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

  private static int randomWithRange(int min, int max) {
    int range = (max - min) + 1;
    return (int)(Math.random() * range) + min;
  }
}

class Client {
  int chatId = 0;
  int hp = 45;
  int fightingChatId = 0;
  Status status = Client.Status.IDLE;

  enum Status {FIGHTING, IDLE};

  Client(int chatId) {
    this.chatId = chatId;
  }
}


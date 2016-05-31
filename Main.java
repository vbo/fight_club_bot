package ChatBot;

import java.lang.InterruptedException;
import java.lang.Thread;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {
  private static final int MAX_HIT = 30;

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
            msg(client, "hi!");
          } else if (txt.equals("stats")) {
            msg(client, "Your hp is " + client.hp + ".\n"
              + "Status is " + client.status);
            //TODO: stats should show your status
          } else if (txt.equals("fight bot")) {
            if (client.status != Client.Status.READY_TO_FIGHT) {
              msg(client, "You need to start a fight first");
            } else {
              client.status = Client.Status.FIGHTING;
              Client bot = new Client(-client.chatId);
              client.fightingChatId = bot.chatId;
              bot.fightingChatId = client.chatId;
              bot.status = Client.Status.FIGHTING;
              bot.isHitReady = true;
              Storage.saveClient(bot.chatId, bot);
              msg(client, "You're now fighting with a bot");
            }
          } else if (txt.equals("fight")) {
            if (client.status != Client.Status.IDLE) {
              msg(client, "You're not idle");
            } else {
              Client opponent = Storage.getOpponentReadyToFight();
              if (opponent == null) {
                msg(client, "You're now waiting for a real opponent");
                client.status = Client.Status.READY_TO_FIGHT;
              } else {
                client.status = Client.Status.FIGHTING;
                opponent.status = Client.Status.FIGHTING;
                client.fightingChatId = opponent.chatId;
                opponent.fightingChatId = client.chatId;
                Storage.saveClient(opponent.chatId, opponent);
                msg(client, "You're now fighting with a real opponent");
                msg(opponent, "You're now fighting with a real opponent");
              }
            }
          } else if (txt.equals("hit")) {
            if (client.status != Client.Status.FIGHTING) {
              msg(client, "You need to start a fight first");
            } else {
              handleHit(client);
            }
          } else if (txt.equals("potion")) {
            client.hp += 10;
            if (client.hp > client.maxHp) {
              client.hp = client.maxHp;
            }
            msg(client, "Potion consumed");
          } else {
            msg(client, "No such command");
          }
          maxUpdateId = upd.update_id;
          Storage.saveMaxUpdateId(maxUpdateId);
          // TODO: dragons here, updateId is written, client is not
          Storage.saveClient(chatId, client);
        }
      }
      List<Client> clientsToRestore = Storage.getOpponentsReadyToRestore();
      for (Client client : clientsToRestore) {
        client.hp += 1;
        if (client.hp == client.maxHp) {
          msg(client, "You are now fully recovered");
        }
        Storage.saveClient(client.chatId, client);
      }
      Thread.sleep(2000); // 10s
    }
  }

  private static void msg(Client client, String message) {
    if (client.chatId < 0) {
      return; //no message for bots
    }
    TelegramApi.say(client.chatId, message);
  }

  private static void handleHit(Client client) {
    assert client != null;
    Client opponent = Storage.getClientByChatId(client.fightingChatId);
    assert opponent != null;
    boolean isBot = opponent.chatId < 0;
    if (opponent.isHitReady) {
        int clientHits = randomWithRange(0, MAX_HIT);
        msg(opponent, "Opponent hits you by " + clientHits + " hp");
        msg(client, "You hit opponent by " + clientHits + " hp");
        int opponentHits = randomWithRange(0, MAX_HIT);
        msg(client, "Opponent hits you by " + opponentHits + " hp");
        msg(opponent, "You hit opponent by " + opponentHits + " hp");
        opponent.hp -= clientHits;
        client.hp -= opponentHits;
        client.isHitReady = false;
        if (!isBot) {
          opponent.isHitReady = false;
        }
        boolean fightFinished = false;
        if (client.hp <= 0) {
          client.hp = 0;
          msg(client, "You died");
          msg(opponent, "Opponent is dead. Congrats!");
          fightFinished = true;
        }
        if (opponent.hp <= 0) {
          opponent.hp = 0;
          msg(opponent, "You died");
          msg(client, "Opponent is dead. Congrats!");
          fightFinished = true;
        }
        if (fightFinished) {
          client.status = Client.Status.IDLE;
          opponent.status = Client.Status.IDLE;
          msg(client, "Fight is finished");
          msg(opponent, "Fight is finished");
        }
    } else {
      client.isHitReady = true;
      msg(client, "Waiting for opponent...");
    }
    Storage.saveClient(opponent.chatId, opponent);
  }

  private static int randomWithRange(int min, int max) {
    int range = (max - min) + 1;
    return (int)(Math.random() * range) + min;
  }
}

class Client {
  int chatId = 0;
  int hp = 45;
  int maxHp = 45;
  int fightingChatId = 0;
  int lastRestore = 0;
  boolean isHitReady = false;
  Status status = Client.Status.IDLE;

  enum Status {FIGHTING, IDLE, READY_TO_FIGHT};

  Client(int chatId) {
    this.chatId = chatId;
  }
}


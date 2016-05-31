package ChatBot;

import java.lang.InterruptedException;
import java.lang.Thread;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Main {
  public static final int HP_UNIT = 5;
  private static final String[] mainButtons = {"fight", "profile"};
  private static final String[] fightButtons = {
    "hit head", "hit torso", "hit legs",
    "block head", "block torso", "block legs"
  };
  private static final String[] levelPointsButtons = {
    "Improve strength", "Improve vitality", "Improve luck"
  };
  private static final String[] botNames = {
    "Ogre", "Grunt", "Skeleton", "Beggar", "Drunk", "Crackhead"
  };

  public static void main(String[] args)
      throws InterruptedException {
    int maxUpdateId = Storage.getMaxUpdateId();
    System.out.println("Starting from updateId " + maxUpdateId);
    while (true) {
      Telegram.Update[] updates = TelegramApi.getUpdates(maxUpdateId + 1);
      Arrays.sort(updates, new Comparator<Telegram.Update>() {
        public int compare(Telegram.Update u1, Telegram.Update u2) {
          return u1.update_id - u2.update_id;
        }
      });
      for (Telegram.Update upd : updates) {
        int chatId = upd.message.chat.id;
        Client client = Storage.getClientByChatId(chatId);
        if (client == null) {
          client = new Client(chatId,
            upd.message.from.first_name + " " + upd.message.from.last_name);
          msg(client, "Welcome to the Fight Club!", mainButtons);
          msg(client, "The 1st rule of the Fight Club is - DO talk about the Fight Club. "
            + "We just started, please feel free to invite your friends if you like it.");
          msg(client, "The 2nd rule of the fight club is - you have to fight to get "
            + "experience and improve your character.");
          msg(client, "We have also picked up a name for you: " + client.username 
            + ". If you don't like it - please change it in your profile.");
          msg(client, "That's it, enjoy!");
        }
        String txt = upd.message.text;
        if (txt.equals("hi")) {
          msg(client, "hi!", mainButtons);
        } else if (txt.equals("profile")) {
          msg(client, getClientStats(client));
          if (client.levelPoints > 0) {
            msg(client, "You have " + client.levelPoints + " unassigned "
              + "level points.", levelPointsButtons);
          }
          if (!client.nameChangeHintSent) {
            msg(client, "You can change your name with the following command "
              + "`/username newname`");
            client.nameChangeHintSent = true;
          }
        } else if (txt.startsWith("/username ")) {
          if (client.status != Client.Status.IDLE) {
            msg(client, "You can change your name only when you're not fighting.");
          }
          String newName = txt.substring(10, txt.length());
          if (newName.matches("[A-z0-9]*")) {
            client.username = newName;
            msg(client, "You name is now " + newName + ".");
          } else {
            msg(client, "Incorrect name, please make sure it has " +
              "english characters and numbers only");
          }
        } else if (txt.equals("Improve strength")) {
          if (client.levelPoints < 1) {
            msg(client, "You have no level points available. You will have some "
              + "when you level up.", mainButtons);
            return;
          }
          client.strength++;
          client.setMaxDamage();
          client.levelPoints--;
          msg(client, "You have increased your strength, it is now "
            + client.strength + ". You have " + client.levelPoints
            + " more level points.", mainButtons);
        } else if (txt.equals("Improve vitality")) {
          if (client.levelPoints < 1) {
            msg(client, "You have no level points available. You will have some "
              + "when you level up.", mainButtons);
            return;
          }
          client.vitality++;
          client.setMaxHp();
          client.levelPoints--;
          msg(client, "You have increased your vitality, it is now "
            + client.vitality + ". You have " + client.levelPoints
            + " more level points.", mainButtons);
        } else if (txt.equals("Improve luck")) {
          if (client.levelPoints < 1) {
            msg(client, "You have no level points available. You will have some "
              + "when you level up.", mainButtons);
            return;
          }
          client.luck++;
          client.levelPoints--;
          msg(client, "You have increased luck, it is now "
            + client.luck + ". You have " + client.levelPoints
            + " more level points.", mainButtons);
        } else if (txt.equals("fight")) {
          if (client.status != Client.Status.IDLE) {
            msg(client, "You're not idle");
          } else {
            Client opponent = Storage.getOpponentReadyToFight();
            if (opponent == null) {
              msg(client, "Searching for a victim...");
              client.status = Client.Status.READY_TO_FIGHT;
              client.readyToFightSince = (int)(System.currentTimeMillis()/1000L); 
            } else {
              client.status = Client.Status.FIGHTING;
              opponent.status = Client.Status.FIGHTING;
              client.fightingChatId = opponent.chatId;
              opponent.fightingChatId = client.chatId;
              Storage.saveClient(opponent.chatId, opponent);
              msg(client, "You're now fighting with " + opponent.username, fightButtons);
              msg(opponent, "You're now fighting with " + client.username, fightButtons);
              msg(client, getClientStats(opponent));
              msg(opponent, getClientStats(client));
            }
          }
        } else if (txt.startsWith("hit ")) {
          String where = txt.substring(4, txt.length());
          Client.BodyPart target = getBodyPartFromString(where);
          if (client.status != Client.Status.FIGHTING) {
            msg(client, "You need to start a fight first");
          } else if (target == null) {
            msg(client, "Don't know how to hit `" + where + "`");
          } else {
            client.hit = target;
            Client opponent = Storage.getClientByChatId(client.fightingChatId);
            assert opponent != null;
            if (readyToHitBlock(client, opponent)) {
              handleHit(client, opponent);
            }
          }
        } else if (txt.startsWith("block ")) {
          String where = txt.substring(6, txt.length());
          Client.BodyPart target = getBodyPartFromString(where);
          if (client.status != Client.Status.FIGHTING) {
            msg(client, "You need to start a fight first");
          } else if (target == null) {
            msg(client, "Don't know how to block `" + where + "`");
          } else {
            client.block = target;
            Client opponent = Storage.getClientByChatId(client.fightingChatId);
            assert opponent != null;
            if (readyToHitBlock(client, opponent)) {
              handleHit(client, opponent);
            }
          }
        } else if (txt.equals("potion")) {
          client.hp = client.maxHp;
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
      List<Client> clientsToRestore = Storage.getClientsReadyToRestore();
      for (Client client : clientsToRestore) {
        client.hp++;
        if (client.hp == client.maxHp) {
          msg(client, "You are now fully recovered");
        }
        Storage.saveClient(client.chatId, client);
      }
      List<Client> readyToFightClients = Storage.getClientsReadyToFight();
      for (Client client : readyToFightClients) {
        client.status = Client.Status.FIGHTING;
        Client bot = new Client(-client.chatId, 
          botNames[rndInRange(0, botNames.length -1)]
        );
        client.fightingChatId = bot.chatId;
        bot.fightingChatId = client.chatId;
        bot.status = Client.Status.FIGHTING;
        generateRandomHitBlock(bot);
        Storage.saveClient(bot.chatId, bot);
        msg(client, "You're now fighting with " + bot.username, fightButtons);
        msg(client, getClientStats(bot));
        Storage.saveClient(client.chatId, client);
      }
      Thread.sleep(2000); // 10s
    }
  }

  private static void generateRandomHitBlock(Client client) {
    Client.BodyPart[] values = Client.BodyPart.values();
    client.hit = values[rndInRange(0, values.length - 1)];
    client.block = values[rndInRange(0, values.length - 1)];
  }

  private static Client.BodyPart getBodyPartFromString(String str) {
    if (str.equals("head")) {
      return Client.BodyPart.HEAD;
    }
    if (str.equals("torso") || str.equals("body")) {
      return Client.BodyPart.TORSO;
    }
    if (str.equals("legs")) {
      return Client.BodyPart.LEGS;
    }
    return null;
  }

  private static boolean readyToHitBlock(Client client, Client opponent) {
    if (client.hit == null) {
      msg(client, "Where do you want to hit?");
      return false;
    }
    if (client.block == null) {
      msg(client, "Where do you want to block?");
      return false;
    }
    if (opponent.hit == null || opponent.block == null) {
      msg(client, "Waiting for " + opponent.username + "...");
      return false;
    }
    return true;
  }

  private static void msg(Client client, String message) {
    msg(client, message, new String[] {});
  }

  private static void msg(Client client, String message, String[] replies) {
    if (client.chatId < 0) {
      return; //no message for bots
    }
    TelegramApi.say(client.chatId, message, replies);
  }

  private static void makeAHit(Client client, Client victim) {
    if (victim.block == client.hit) {
      msg(victim, "Nice! You have blocked the " + client.username + "'s attack");
      msg(client, "Damn! Your attack was blocked");
      return;
    }
    int clientHits = getDamage(client);
    victim.hp = Math.max(victim.hp - clientHits, 0);
    if (clientHits > client.maxDamage) {
      msg(victim, "Ouch! " + client.username + " makes a critical hit!");
      msg(client, "Wow! You make a critical hit!");
    }
    msg(victim, client.username + " hits your " + client.hit + " by "
      + clientHits + " hp, you have now " + victim.hp + " hp left.");
    msg(client, "You hit " + victim.username + "'s " + client.hit + " by "
      + clientHits + " hp, " + victim.hp + " hp left.");
  }

  private static void handleHit(Client client, Client opponent) {
    boolean isBot = opponent.chatId < 0;
    // Who goes first
    Client first = client;
    Client second = opponent;
    int clientValue = client.luck;
    int opponentValue = opponent.luck;
    if (clientValue == opponentValue) {
      clientValue = rndInRange(0, 100);
      opponentValue = rndInRange(0, 100);
    }
    if (clientValue > opponentValue) {
      first = client;
      second = opponent;
    } else {
      first = opponent;
      second = client;
    }
    // Making hits
    makeAHit(first, second);
    if (second.hp == 0) {
      msg(first, "Lucky you! You didn't get any damage because your "
        + second.username + " died.");
      msg(second, "Oops, you didn't have much time to attack.");
    } else {
      makeAHit(second, first);
    }
    client.hit = null;
    client.block = null;
    if (!isBot) {
      opponent.hit = null;
      client.block = null;
    } else {
      generateRandomHitBlock(opponent);
    }
    // Finish fight if needed
    boolean fightFinished = false;
    Client winner = null;
    Client loser = null;
    if (client.hp <= 0 && opponent.hp <= 0) {
      msg(client, "Everybody died in this fight =(");
      msg(opponent, "Everybody died in this fight =(");
      fightFinished = true;
      client.hp = 0;
      opponent.hp = 0;
    } else {
      if (client.hp <= 0) {
        winner = opponent;
        loser = client;
      }
      if (opponent.hp <= 0) {
        winner = client;
        loser = opponent;
      }
    }
    if (winner != null) {
      loser.hp = 0;
      msg(loser, "You died");
      msg(winner, opponent.username + " is dead. Congrats!");
      fightFinished = true;
      winner.fightsWon++;
      winner.exp += 10 * (loser.level + 1);
      msg(winner, "You gained " + 10 * (loser.level + 1) + " experience.");
    }
    if (winner != null || fightFinished) {
      client.status = Client.Status.IDLE;
      opponent.status = Client.Status.IDLE;
      client.totalFights++;
      msg(client, "Fight is finished", mainButtons);
      msg(opponent, "Fight is finished", mainButtons);
      levelUpIfNeeded(client);
      levelUpIfNeeded(opponent);
    }
    Storage.saveClient(opponent.chatId, opponent);
  }

  private static String getClientStats(Client client) {
    return "*" + client.username + "*\n" 
      + "Status: " + client.status + "\n"
      + "Level: " + client.level + "\n"
      + "Health: " + client.hp + " (out of " + client.maxHp + ")\n"
      + "Damage: 1 - " + client.maxDamage + "\n"
      + "Strength: " + client.strength  + "\n"
      + "Vitality: " + client.vitality + "\n"
      + "Luck: " + client.luck + "\n"
      + "Experience: " + client.exp + " "
      + "(" + nextExp(client) + " needed to level up)\n"
      + "Fights won: " + client.fightsWon + " "
      + "(out of " + client.totalFights + ")\n";
  }

  private static int getDamage(Client client) {
    int critRnd = rndInRange(0, 30);
    if (critRnd < client.luck) {
      return client.maxDamage + rndInRange(0, client.maxDamage);
    }
    return rndInRange(0, client.maxDamage);
  }

  private static void levelUpIfNeeded(Client client) {
    if (client.exp >= nextExp(client)) {
      client.level++;
      client.levelPoints++;
      msg(client, "You have achieved level " + client.level + "!\n",
        levelPointsButtons);
    }
  }

  private static int nextExp(Client client) {
    int levelDelta = 30;
    int result = 0;
    for (int i = 0; i < client.level + 1; i++) {
      result = result + levelDelta * (int)Math.pow(2, i);
    }
    return result;
  }

  private static int rndInRange(int min, int max) {
    int range = (max - min) + 1;
    return (int)(Math.random() * range) + min;
  }
}

class Client {
  enum Status {FIGHTING, IDLE, READY_TO_FIGHT};
  enum BodyPart {HEAD, TORSO, LEGS};
  String username;
  int chatId = 0;
  boolean nameChangeHintSent = false;
  BodyPart hit = null;
  BodyPart block = null;
  Status status = Client.Status.IDLE;
  int fightingChatId = 0;
  int lastRestore = 0;
  int readyToFightSince = 0;

  int totalFights = 0;
  int fightsWon = 0;

  int exp = 0;
  int level = 0;
  int strength = 3;
  int vitality = 3;
  int luck = 3;
  int levelPoints = 0;

  int hp;
  int maxHp;
  int maxDamage;

  Client(int chatId, String username) {
    this.chatId = chatId;
    this.username = username;
    setMaxHp();
    hp = maxHp;
    setMaxDamage();
  }

  public void setMaxHp() {
    maxHp = 9 * Main.HP_UNIT + (vitality - 3) * Main.HP_UNIT;
  }

  public void setMaxDamage() {
    maxDamage = strength * Main.HP_UNIT;
  }
}


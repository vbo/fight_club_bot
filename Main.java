package ChatBot;

import java.lang.InterruptedException;
import java.lang.Thread;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
  public static final int HP_UNIT = 5;
  private static final String[] mainButtons = {"fight", "profile", "wiseman"};
  private static final String[] fightButtons = {
    "hit head", "hit torso", "hit legs",
    "block head", "block torso", "block legs"
  };
  private static final String[] levelPointsButtons = {
    "improve strength", "improve vitality", "improve luck"
  };
  private static final String[] botNames = {
    "Ogre", "Grunt", "Skeleton", "Beggar", "Drunk", "Crackhead"
  };
  private static Set<Integer> activeChats = new HashSet<>();
  private static boolean isProd = false;
  private static int curTime;

  public static void main(String[] args)
      throws InterruptedException, Exception {
    if (args.length < 1) {
      System.out.println("Usage: ChatBot.jar path/to/db");
      System.exit(0);
    }
    Logger.setDbPath(args[0]);

    if (args.length > 1 && args[1].equals("PROD")) {
      isProd = true;
      TelegramApi.token = TelegramApi.TOKEN_PROD;
    }

    System.out.println("Fight Club Server started...");
    while (true) {
      try {
        int maxUpdateId = Storage.getMaxUpdateId();
        // TODO: download updates async and put to queue
        Telegram.Update[] updates = TelegramApi.getUpdates(maxUpdateId + 1);
        Arrays.sort(updates, new Comparator<Telegram.Update>() {
          public int compare(Telegram.Update u1, Telegram.Update u2) {
            return u1.update_id - u2.update_id;
          }
        });
        // Handle user commands
        for (Telegram.Update upd : updates) {
          Storage.saveMaxUpdateId(upd.update_id);
          if (upd.message != null && upd.message.text != null) {
            handleUpdate(upd);
          }
        }
        // Background/async operations for each client
        curTime = (int)(System.currentTimeMillis() / 1000L);
        Storage.forEachClient(new ClientDo() {
          public void run(Client client) {
            if (client == null) {
              return; // this shouldn't happen
            }
            if (client.chatId < 0) {
              return; // bots have no async logic as of now
            }
            handleClientAsync(client);
          }
        });
      } catch (Exception e) {
        if (isProd) {
          Logger.logException(e);
        } else {
          throw e;
        }
      }
      Thread.sleep(500); // 2s 
    }
  }

  private static void handleClientAsync(Client client) {
    boolean clientChanged = false;
    // Fight bot if for 10 seconds there is no human opponent
    if (client.status == Client.Status.READY_TO_FIGHT
        && client.readyToFightSince <= curTime - 10) {
      Client bot = new Client(-client.chatId, 
        botNames[Utils.rndInRange(0, botNames.length -1)],
        client
      );
      setFightingStatus(client, bot);
      setFightingStatus(bot, client);
      generateRandomHitBlock(bot);
      Storage.saveClient(bot.chatId, bot);
      Storage.saveClient(client.chatId, client);

      msg(client, "You're now fighting with " + bot.username + ".", fightButtons);
      msg(client, getClientStats(bot));
      sendFightInstruction(client);
    }
    // Recover hp over time
    if (client.status == Client.Status.IDLE
        && client.hp < client.getMaxHp()
        && client.lastRestore <= curTime - 3) {
      client.hp++;
      client.lastRestore = curTime;
      if (client.hp == client.getMaxHp()) {
        msg(client, "You are now fully recovered.");
      }
      Storage.saveClient(client.chatId, client);
    }
    // Check for slow acting (warning)
    if (client.status == Client.Status.FIGHTING
        && (client.hit == null || client.block == null)
        && !client.timeoutWarningSent
        && client.lastFightActivitySince <= curTime - 30) {
      client.timeoutWarningSent = true;
      Storage.saveClient(client.chatId, client);

      msg(client, "You have 5 seconds to make a decision.");
    }
    // Check for slow acting (finish fight)
    if (client.status == Client.Status.FIGHTING
        && client.timeoutWarningSent
        && client.lastFightActivitySince <= curTime - 50) {
      Client opponent = Storage.getClientByChatId(client.fightingChatId);
      msg(client, "Timeout!");
      msg(opponent, "Timeout!");
      finishFight(opponent, client); 
      Storage.saveClient(opponent.chatId, opponent);
      Storage.saveClient(client.chatId, client);
    }
  }

  private static void handleUpdate(Telegram.Update upd) {
    curTime = (int)(System.currentTimeMillis() / 1000L);
    int chatId = upd.message.chat.id;
    Client client = Storage.getClientByChatId(chatId);
    boolean newClient = client == null;
    if (newClient) {
      String username = upd.message.from.first_name;
      if (upd.message.from.last_name != null) {
        username = username + " " + upd.message.from.last_name;
      }
      client = new Client(chatId, username);
    }
    client.lastActivity = curTime;
    activeChats.add(chatId);
    Storage.saveClient(chatId, client);

    if (newClient) {
      msg(client, "Welcome to the Fight Club!", mainButtons);
      sendToActiveUsers(client.username + " joined the Fight Club!");
    }

    String txt = upd.message.text;

    if (txt.equals("/start")) {
      return;
    }

    if (txt.equals("wiseman") || txt.equals("/wiseman")) {
      msg(client, PhraseGenerator.getWisdom(client)); 
      return;
    }

    if (txt.equals("profile") || txt.equals("/profile")) {
      showProfile(client);
      return;
    }

    if (txt.startsWith("/username ")) {
      if (client.status != Client.Status.IDLE) {
        msg(client, "You can change your name only when you're not fighting.");
        return;
      }
      String newName = txt.substring(10, txt.length());
      if (!newName.matches("[A-z0-9]*")) {
        msg(client, "Incorrect name, please make sure it has " +
          "english characters and numbers only.");
        return;
      }
      changeUserName(client, newName);
      return;
    }

    if (txt.startsWith("improve ")) {
      String what = txt.substring(8, txt.length());
      if (client.levelPoints < 1) {
        msg(client, "You have no level points available. You will have some "
          + "when you level up.", mainButtons);
        return;
      }
      improveSkill(client, what);
      return;
    }

    if (txt.equals("fight") || txt.equals("/fight")) {
      if (client.status == Client.Status.FIGHTING) {
        msg(client, "You're already fighiting with somebody.");
        return;
      } 
      if (client.status == Client.Status.READY_TO_FIGHT) {
        msg(client, "You're already searching for a victim.");
        return;
      }
      // TODO: this does linear search through all clients :(
      Client opponent = Storage.getOpponentReadyToFight();
      if (opponent == null) {
        setReadyToFight(client);
      } else { 
        startFightReal(client, opponent);
      }
      return;
    }
    if (txt.startsWith("hit ")) {
      String where = txt.substring(4, txt.length());
      Client.BodyPart target = getBodyPartFromString(where);
      if (client.status != Client.Status.FIGHTING) {
        msg(client, "You need to start a fight first.", mainButtons);
        return;
      } 
      if (target == null) {
        msg(client, "Don't know how to hit `" + where + "`.");
        return;
      }
      setHit(client, target);
      return;
    }

    if (txt.startsWith("block ")) {
      String where = txt.substring(6, txt.length());
      Client.BodyPart target = getBodyPartFromString(where);
      if (client.status != Client.Status.FIGHTING) {
        msg(client, "You need to start a fight first.");
        return;
      }
      if (target == null) {
        msg(client, "Don't know how to block `" + where + "`.");
        return;
      }
      setBlock(client, target);
      return;
    }

    if (txt.equals("/potion42")) {
      consumePotion(client);
      return;
    } 

    if (txt.equals("/retreat42")) {
      if (client.status != Client.Status.FIGHTING) {
        return;
      }
      Client opponent = Storage.getClientByChatId(client.fightingChatId);
      msg(client, "Retreat42!");
      msg(opponent, "Retreat42!");
      finishFight(opponent, client); 
      Storage.saveClient(opponent.chatId, opponent);
      Storage.saveClient(client.chatId, client);
      return;
    } 

    if (client.status == Client.Status.FIGHTING &&
        !txt.startsWith("/")) {
      String message = txt; 
      Client opponent = Storage.getClientByChatId(client.fightingChatId);
      String sayingPhrase = PhraseGenerator.getSayingPhrase(client, message, opponent);
      msg(client, sayingPhrase);
      msg(opponent, sayingPhrase);
      return;
    }

    if (!txt.startsWith("/")) {
      String message = "\uD83D\uDCE2 " + client.username + ": " + txt; 
      int numListeners = sendToActiveUsers(message) - 1;
      if (numListeners == 0) {
        msg(client, "You were not heard by anyone :(");
      }
      return;
    }

    // TODO: Add help page link here
    msg(client, "Use buttons below to make valid actions.");
  }

  // returns number of people who heard you
  private static int sendToActiveUsers(String message) {
    int numListeners = 0;
    List<Integer> passive = new LinkedList<>();
    for (int recepientChatId : activeChats) {
      Client recepient = Storage.getClientByChatId(recepientChatId);
      if (recepient.lastActivity > curTime - 600) {
        msg(recepient, message);
        numListeners++;
      } else {
        passive.add(recepientChatId);
      }
    }
    for (int passiveChatId : passive) {
      activeChats.remove(passiveChatId);
    }
    return numListeners; 
  }

  private static void showProfile(Client client) {
    msg(client, getClientStats(client));
    if (client.levelPoints > 0) {
      msg(client, "You have " + client.levelPoints + " unassigned "
        + "level points.", levelPointsButtons);
    }
    if (!client.nameChangeHintSent) {
      msg(client, "You can change your name with the following command \n"
        + "`/username newname`.");
      client.nameChangeHintSent = true;
    }
    Storage.saveClient(client.chatId, client);
  }

  private static void changeUserName(Client client, String newName) {
    client.username = newName;
    msg(client, "Your name is now " + newName + ".");
    Storage.saveClient(client.chatId, client);
  }

  private static void generateRandomHitBlock(Client client) {
    Client.BodyPart[] values = Client.BodyPart.values();
    client.hit = values[Utils.rndInRange(0, values.length - 1)];
    client.block = values[Utils.rndInRange(0, values.length - 1)];
  }

  private static void improveSkill(Client client, String skill) {
    int newValue = 0;
    if (skill.equals("strength")) {
      newValue = ++client.strength;
    } else if (skill.equals("vitality")) {
      newValue = ++client.vitality;
    } else if (skill.equals("luck")) {
      newValue = ++client.luck;
    }
    if (newValue == 0) {
      msg(client, "Don't know how to improve " + skill + ".");
      return;
    } 
    client.levelPoints--;
    msg(client, "You have increased your " + skill + ", it is now "
      + client.strength + ". You have " + client.levelPoints
      + " more level points.", mainButtons);
    Storage.saveClient(client.chatId, client);
  }

  private static void setReadyToFight(Client client) {
    // TODO: set ready to fight and save to index
    client.status = Client.Status.READY_TO_FIGHT;
    client.readyToFightSince = curTime; 
    Storage.saveClient(client.chatId, client);
    sendToActiveUsers(PhraseGenerator.getReadyToFightPhrase(client));
  }

  private static void startFightReal(Client client, Client opponent) {
    setFightingStatus(client, opponent);
    setFightingStatus(opponent, client);

    // Save automically both of them
    Storage.saveClient(client.chatId, client);
    Storage.saveClient(opponent.chatId, opponent);
    msg(client, "You're now fighting with " + opponent.username + ".", fightButtons);
    msg(opponent, "You're now fighting with " + client.username + ".", fightButtons);
    msg(client, getClientStats(opponent));
    msg(opponent, getClientStats(client));
    sendFightInstruction(client);
    sendFightInstruction(opponent);
  }

  private static void sendFightInstruction(Client client) {
    if (client.fightsWon == 0) {
      msg(client, "You need to choose which part of your body to block and where to hit.");
    }
  }

  private static void setHit(Client client, Client.BodyPart target) {
    client.hit = target;
    client.lastFightActivitySince = curTime;
    client.timeoutWarningSent = false;
    Client opponent = Storage.getClientByChatId(client.fightingChatId);
    assert opponent != null;
    if (readyToHitBlock(client, opponent)) {
      handleHit(client, opponent);
    }
    Storage.saveClient(client.chatId, client);
    Storage.saveClient(opponent.chatId, opponent);
  }

  private static void setBlock(Client client, Client.BodyPart target) {
    client.block = target;
    client.lastFightActivitySince = curTime;
    client.timeoutWarningSent = false;
    Client opponent = Storage.getClientByChatId(client.fightingChatId);
    assert opponent != null;
    if (readyToHitBlock(client, opponent)) {
      handleHit(client, opponent);
    }
    // TODO: it's not an atomic operation to save two clients.
    Storage.saveClient(client.chatId, client);
    Storage.saveClient(opponent.chatId, opponent);
  }

  private static void consumePotion(Client client) {
    client.hp = client.getMaxHp();
    if (client.hp > client.getMaxHp()) {
      client.hp = client.getMaxHp();
    }
    msg(client, "Potion consumed.");
    Storage.saveClient(client.chatId, client);
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

  private static void msg(int chatId, String message) {
    TelegramApi.say(chatId, message, new String[] {});
  }

  private static void msg(Client client, String message, String[] replies) {
    if (client.chatId < 0) {
      return; //no message for bots
    }
    TelegramApi.say(client.chatId, message, replies);
  }

  private static void makeAHit(Client client, Client victim) {
    String hitPhrase = "";
    String clientPrefix = "\uD83D\uDDE1 ";
    String victimPrefix = "\uD83D\uDEE1 ";
    if (victim.block == client.hit) {
      hitPhrase =
        PhraseGenerator.getBlockPhrase(client, victim, client.hit);  
      msg(victim, victimPrefix + hitPhrase);
      msg(client, clientPrefix + hitPhrase);
      return;
    }
    int clientHits = getDamage(client);
    victim.hp = Math.max(victim.hp - clientHits, 0);
    if (clientHits == 0) {
      hitPhrase =
        PhraseGenerator.getMissPhrase(client, victim, client.hit);
      msg(victim, victimPrefix + hitPhrase);
      msg(client, clientPrefix + hitPhrase);
      return;
    }
    hitPhrase = PhraseGenerator.getHitPhrase(
      client,
      victim,
      client.hit,
      clientHits > client.getMaxDamage(),
      clientHits
    );
    msg(victim, victimPrefix + hitPhrase);
    msg(client, clientPrefix + hitPhrase);
  }

  private static void handleHit(Client client, Client opponent) {
    boolean isBot = opponent.chatId < 0;
    // Who goes first
    Client first = client;
    Client second = opponent;
    int clientValue = client.luck;
    int opponentValue = opponent.luck;
    if (clientValue == opponentValue) {
      clientValue = Utils.rndInRange(0, 100);
      opponentValue = Utils.rndInRange(0, 100);
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
      msg(first, "Lucky you! You didn't get any damage because "
        + second.username + " is defeated.");
      msg(second, "Oops, you didn't have much time to attack.");
    } else {
      makeAHit(second, first);
    }
    client.hit = null;
    client.block = null;
    if (!isBot) {
      opponent.hit = null;
      opponent.block = null;
    } else {
      generateRandomHitBlock(opponent);
    }
    // Finish fight if needed
    Client winner = null;
    Client loser = null;
    if (client.hp <= 0) {
      winner = opponent;
      loser = client;
    }
    if (opponent.hp <= 0) {
      winner = client;
      loser = opponent;
    }
    if (winner != null) {
      loser.hp = 0;
      finishFight(winner, loser);
    }
    Storage.saveClient(opponent.chatId, opponent);
  }

  private static void finishFight(Client winner, Client loser) {
    winner.fightsWon++;
    winner.totalFights++;
    loser.totalFights++;
    int expGained = getExperience(loser);  
    winner.exp += expGained; 
    winner.status = Client.Status.IDLE;
    loser.status = Client.Status.IDLE;
    winner.timeoutWarningSent = false;
    loser.timeoutWarningSent = false;
    sendToActiveUsers(PhraseGenerator.getWonPhrase(winner, loser));
    msg(winner, "You gained " + expGained + " experience.");
    if (winner.hp < winner.getMaxHp()) {
      msg(winner, "Fight is finished. Your health will recover in "
        + 3*(winner.getMaxHp() - winner.hp) + " seconds.", mainButtons);
    } else {
      msg(winner, "Fight is finished.", mainButtons);
    }
    if (loser.hp < loser.getMaxHp()) {
      msg(loser, "Fight is finished. Your health will recover in "
        + 3*(loser.getMaxHp() - loser.hp) + " seconds.", mainButtons);
    } else {
      msg(loser, "Fight is finished.", mainButtons);
    }
    levelUpIfNeeded(winner);
    levelUpIfNeeded(loser);
  }

  private static int getExperience(Client loser) {
    return 10 * (loser.level + 1);
  }

  private static String getClientStats(Client client) {
    String result = "*" + client.username + "*\n" 
      + "Level: " + (client.level + 1) + "\n"
      + "Health: " + client.hp + " (out of " + client.getMaxHp() + ")\n"
      + "Damage: 1 - " + client.getMaxDamage() + "\n"
      + "Strength: " + client.strength  + "\n"
      + "Vitality: " + client.vitality + "\n"
      + "Luck: " + client.luck;
    if (client.chatId > 0) {  
      result += "\n"
        + "Experience: " + client.exp + " "
        + "(" + nextExp(client) + " needed to level up)\n"
        + "Fights won: " + client.fightsWon + " "
        + "(out of " + client.totalFights + ")\n";
    }
    return result;
  }

  private static int getDamage(Client client) {
    int critRnd = Utils.rndInRange(0, 30);
    if (critRnd < client.luck) {
      return client.getMaxDamage() + Utils.rndInRange(0, client.getMaxDamage());
    }
    return Utils.rndInRange(0, client.getMaxDamage());
  }

  private static void levelUpIfNeeded(Client client) {
    if (client.exp >= nextExp(client)) {
      client.level++;
      client.levelPoints++;
      msg(client, "You have achieved level " + client.level + "!\n",
        levelPointsButtons);
    }
  }

  private static void setFightingStatus(Client client, Client opponent) {
    client.status = Client.Status.FIGHTING;
    client.fightingChatId = opponent.chatId;
    client.lastFightActivitySince = curTime; 
    client.timeoutWarningSent = false;
  }

  private static int nextExp(Client client) {
    int levelDelta = 30;
    int result = 0;
    for (int i = 0; i < client.level + 1; i++) {
      result = result + levelDelta * (int)Math.pow(2, i);
    }
    return result;
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
  int lastFightActivitySince = 0;
  int lastActivity = 0;
  boolean timeoutWarningSent = false;

  int totalFights = 0;
  int fightsWon = 0;

  int exp = 0;
  int level = 0;
  int strength = 3;
  int vitality = 3;
  int luck = 3;
  int levelPoints = 0;

  int hp;

  Client(int chatId, String username) {
    this.chatId = chatId;
    this.username = username;
    hp = getMaxHp();
  }

  Client(int chatId, String username, Client opponent) {
    this(chatId, username);
    if (opponent.level == 0) {
      vitality = 1;
      strength = 2;
      luck = 1;
    } else {
      int k = 1;
      if (Utils.rndInRange(0, opponent.totalFights) > opponent.fightsWon) {
        k *= -1;
      }
      this.level = Math.max(opponent.level + k*Utils.rndInRange(0, 4), 0);
      for (int i = 0; i < this.level; i++) {
        int ch = Utils.rndInRange(1, 3);
        if (ch == 1) {
          this.strength++;
        } else if (ch == 2) {
          this.vitality++;
        } else {
          this.luck++;
        }
      }
    }
    hp = getMaxHp();
  }

  public int getMaxHp() {
    return 9 * Main.HP_UNIT + (vitality - 3) * Main.HP_UNIT;
  }

  public int getMaxDamage() {
    return Main.HP_UNIT + strength - 3;
  }
}


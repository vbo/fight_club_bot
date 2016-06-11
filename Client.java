package ChatBot;

import java.util.List;
import java.util.LinkedList;

class Client {
  private static final BotConfig[] botTypes = {
    new BotConfig("Beggar", new int[] {1,3}, new int[] {1,1,6}),
    new BotConfig("Drunk", new int[] {1,3}, new int[] {1,6,1}),
    new BotConfig("Crackhead", new int[] {1,3}, new int[] {6,1,1}),
    new BotConfig("Skeleton", new int[] {4,6}, new int[] {4,4,1}),
    new BotConfig("Zombie", new int[] {4,6}, new int[] {6,1,1}),
    new BotConfig("Ghost", new int[] {4,6}, new int[] {1,1,6}),
    new BotConfig("Mummy", new int[] {7,9}, new int[] {1,1,1}),
    new BotConfig("Lich", new int[] {7,9}, new int[] {4,4,1}),
    new BotConfig("Vampire", new int[] {7,9}, new int[] {1,6,1}),
    new BotConfig("Ghoul", new int[] {10,12}, new int[] {6,1,1}),
    new BotConfig("Undead", new int[] {10,12}, new int[] {1,6,1}),
    new BotConfig("Necromant", new int[] {10,12}, new int[] {1,1,6}),
    new BotConfig("Devil", new int[] {12,20}, new int[] {1,1,6}),
    new BotConfig("Demon", new int[] {12,20}, new int[] {1,1,1})
  };

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
  int level = 1;
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

  // Used for creating bots
  Client(int chatId, Client opponent) {
    this.chatId = chatId;
    if (opponent.level == 1) {
      vitality = 1;
      strength = 1;
      luck = 1;
      level = 1;
    } else {
      int k = 1;
      if (Utils.rndInRange(0, opponent.totalFights) > opponent.fightsWon) {
        k *= -1;
      }
      level = Math.max(opponent.level + k*Utils.rndInRange(0, 4), 1);
    }
    BotConfig bc = pickBotType();
    this.username = bc.name;
    for (int i = 0; i < this.level; i++) {
      int ch = Utils.rndInRangeWeighted(bc.characteristics);
      if (ch == 0) {
        strength++;
      } else if (ch == 1) {
        vitality++;
      } else {
        luck++;
      }
    }
    hp = getMaxHp();
  }

  private BotConfig pickBotType() {
    List<BotConfig> eligible = new LinkedList<>();
    for (BotConfig bc : botTypes) {
      if (level >= bc.minLevel && level <= bc.maxLevel) {
        eligible.add(bc);
      }
    }
    return Utils.getRnd(eligible.toArray(new BotConfig[0]));
  }

  public int getMaxHp() {
    return 9 * Main.HP_UNIT + (vitality - 3) * Main.HP_UNIT;
  }

  public int getMaxDamage() {
    return Main.HP_UNIT + strength - 3;
  }
}

class BotConfig {
  int minLevel, maxLevel;
  int[] characteristics;
  String name;

  BotConfig(String name, int[] levels, int[] characteristics) {
    this.name = name;
    this.minLevel = levels[0];
    this.maxLevel = levels[1];
    this.characteristics = characteristics;
  }
}


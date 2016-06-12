package ChatBot;

class Game {
  static final BotConfig[] BOT_TYPES = {
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

  enum Item {
    // TODO: when an item is added to the list - clients needs to be updated with a
    // larger array.
    COIN("coin", "coins"), BOTTLE ("bottle", "bottles"), 
    CRACK("gr of crack", "gr of crack"), BONE("bone", "bones"),
    FLESH("piece of flesh", "pieces of flesh"), SOUL_STONE("soul stone", "soul stones"),
    BANDAGE("bandage", "bandages"), WAX("gr of wax", "gr of wax"),
    FANG("fang", "fangs"), CLAW("claw", "claws"), ASH("gr of ash", "gr of ash"),
    PAPER("sheet of paper", "sheets of paper"), SILVER("silver piece", "silver pieces"),
    GOLD("golden piece", "golden pieces"), POTION("healing potion", "healing potions");
    
    String singular, plural;

    private Item(String singular, String plural) {
      this.singular = singular;
      this.plural = plural;
    }
  };

  static final Item[] ITEM_VALUES = Item.values();
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


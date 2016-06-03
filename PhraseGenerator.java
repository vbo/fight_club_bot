package ChatBot;

class PhraseGenerator {
  static String getHitPhrase(Client offender, Client victim, Client.BodyPart part,
      boolean critHit, int damage) {
    String result = 
        victim.username + " "
      + Utils.getRnd(Phrases.wasDoingSomething) + ", "
      + Utils.getRnd(Phrases.when) + " "
      + Utils.getRnd(Phrases.adjective) + " "
      + offender.username +  " " 
      + Utils.getRnd(Phrases.hit[part.ordinal()]) + ". ";
    if (critHit) {
      result += "Critical hit! ";
    }
    result += "-" + damage + " [[" + victim.hp + "/" + victim.getMaxHp() + "]]";
    return result;
  }

  static String getBlockPhrase(Client offender, Client victim, Client.BodyPart part) {
    String result =
        offender.username + " "
      + Utils.getRnd(Phrases.wasTrying) + " "
      + Utils.getRnd(Phrases.toHit[part.ordinal()]) + " "
      + Utils.getRnd(Phrases.but) + " "
      + victim.username +  " "
      + Utils.getRnd(Phrases.blocked) + ". " 
      + "[[" + victim.hp + "/" + victim.getMaxHp() + "]]";
    return result;
  }

  static String getMissPhrase(Client offender, Client victim, Client.BodyPart part) {
    String result =
        offender.username + " "
      + Utils.getRnd(Phrases.wasTrying) + " "
      + Utils.getRnd(Phrases.toHit[part.ordinal()]) + " "
      + Utils.getRnd(Phrases.but) +  " "
      + Utils.getRnd(Phrases.missed) + ". " 
      + "[[" + victim.hp + "/" + victim.getMaxHp() + "]]";
    return result;
  }


  static String getSayingPhrase(Client teller, String msg) {
    return teller.username + " " + Utils.getRnd(Phrases.said) + ": " + msg;
  }
};

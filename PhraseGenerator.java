package ChatBot;

class PhraseGenerator {

  static String getReadyToFightPhrase(Client client) {
    return "\u2694 " + client.username + " " + Utils.getRnd(Phrases.lookingForOpponent) + "."; 
  }

  static String getWonPhrase(Client winner, Client loser) {
    return "\u2620 " + winner.username + " " + Utils.getRnd(Phrases.won) + " "
      + loser.username + ".";
  }

  static String getWisdom(Client client) {
    return Utils.getRnd(Phrases.wisdomIntro) + " "
      + Utils.getRnd(Phrases.wisdoms);
  }

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


  static String getSayingPhrase(Client teller, String msg, Client listener) {
    return teller.username + " " + Utils.getRnd(Phrases.said) + " "
      + listener.username + ": " + msg;
  }
};

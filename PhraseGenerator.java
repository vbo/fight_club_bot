package ChatBot;

class PhraseGenerator {

  static String getReadyToFightPhrase(Client client) {
    return "\u2694 " + client.username + " " +
      Utils.getRnd(Phrases.combatTexts.lookingForOpponent.get("en")) + ".";
  }

  static String getWonPhrase(Client winner, Client loser) {
    return "\u2620 " + winner.username + " " +
      Utils.getRnd(Phrases.combatTexts.won.get("en")) + " " +
      loser.username + ".";
  }

  static String getWisdom(Client client) {
    return Utils.getRnd(Phrases.wiseTexts.wisdomIntro.get(client.lang)) + " "
      + Utils.getRnd(Phrases.wiseTexts.wisdoms.get(client.lang));
  }

  static String getHitPhrase(Client offender,
                             Client victim,
                             Client.BodyPart part,
                             boolean critHit,
                             int damage) {
    String result =
        victim.username + " "
      + Utils.getRnd(Phrases.combatTexts.wasDoingSomething.get("en")) +
      ", " + Utils.getRnd(Phrases.combatTexts.when.get("en")) + " "
      + Utils.getRnd(Phrases.combatTexts.adjective.get("en")) + " "
      + offender.username +  " "
      + Utils.getRnd(Phrases.combatTexts.hit.get("en")[part.ordinal()]) + ". ";
    if (critHit) {
      result += "Critical hit! ";
    }
    result += "-" + damage + " [" + victim.hp + "/" + victim.getMaxHp() + "]";
    return result;
  }

  static String getBlockPhrase(Client offender,
                               Client victim,
                               Client.BodyPart part) {
    String result =
        offender.username + " "
      + Utils.getRnd(Phrases.combatTexts.wasTrying.get("en")) + " "
      + Utils.getRnd(Phrases.combatTexts.toHit.get("en")[part.ordinal()])
      + " " + Utils.getRnd(Phrases.combatTexts.but.get("en")) + " "
      + victim.username +  " "
      + Utils.getRnd(Phrases.combatTexts.blocked.get("en")) + ". "
      + "[" + victim.hp + "/" + victim.getMaxHp() + "]";
    return result;
  }

  static String getMissPhrase(Client offender,
                              Client victim,
                              Client.BodyPart part) {
    String result =
        offender.username + " "
      + Utils.getRnd(Phrases.combatTexts.wasTrying.get("en")) + " "
      + Utils.getRnd(Phrases.combatTexts.toHit.get("en")[part.ordinal()])
      + " " + Utils.getRnd(Phrases.combatTexts.but.get("en")) +  " "
      + Utils.getRnd(Phrases.combatTexts.missed.get("en")) + ". "
      + "[" + victim.hp + "/" + victim.getMaxHp() + "]";
    return result;
  }


  static String getSayingPhrase(Client teller, String msg, Client listener) {
    return teller.username + " " +
      Utils.getRnd(Phrases.combatTexts.said.get("en")) + " " +
      listener.username + ": " + msg;
  }
};

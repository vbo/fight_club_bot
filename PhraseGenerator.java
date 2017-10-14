package ChatBot;

import java.util.HashMap;
import java.util.Map;

class PhraseGenerator {

  static Map<String, String> getJoinedTheFightClub(String username) {
    Map<String, String> result = new HashMap<>();
    for (String lang : Phrases.miscTexts.joinedTheFightClub.keySet()) {
      result.put(lang, username + " " +
        Phrases.miscTexts.joinedTheFightClub.get(lang));
    }
    return result;
  }

  static Map<String, String> getReadyToFightPhrase(Client client) {
    Map<String, String> result = new HashMap<>();
    for (String lang : Phrases.combatTexts.lookingForOpponent.keySet()) {
      result.put(lang, "\u2694 " + client.username + " " +
        Utils.getRnd(Phrases.combatTexts.lookingForOpponent.get(lang)) + ".");
    }
    return result;
  }

  static Map<String, String> getWonPhrase(Client winner, Client loser) {
    Map<String, String> result = new HashMap<>();
    for (String lang : Phrases.combatTexts.lookingForOpponent.keySet()) {
      result.put(lang, "\u2620 " + winner.username + " " +
        Utils.getRnd(Phrases.combatTexts.won.get(lang)) + " " +
        loser.username + ".");
    }
    return result;
  }

  static Map<String, String> getWisdom(Client client) {
    Map<String, String> result = new HashMap<>();
    for (String lang : Phrases.combatTexts.lookingForOpponent.keySet()) {
      result.put(lang,
        Utils.getRnd(Phrases.wiseTexts.wisdomIntro.get(client.lang)) +
        " " + Utils.getRnd(Phrases.wiseTexts.wisdoms.get(client.lang)));
    }
    return result;
  }

  static Map<String, String> getHitPhrase(Client offender,
                             Client victim,
                             Client.BodyPart part,
                             boolean critHit,
                             int damage) {
    Map<String, String> result = new HashMap<>();
    for (String lang : Phrases.combatTexts.lookingForOpponent.keySet()) {
      // Don't think about StringBuilder, it's a lie.
      // https://stackoverflow.com/questions/4965513/stringbuilder-vs-string-considering-replace 
      String tmp =
          victim.username + " " +
        Utils.getRnd(Phrases.combatTexts.wasDoingSomething.get(lang)) +
        ", " + Utils.getRnd(Phrases.combatTexts.when.get(lang)) + " " +
        Utils.getRnd(Phrases.combatTexts.adjective.get(lang)) + " " +
        offender.username +  " " +
        Utils.getRnd(Phrases.combatTexts.hit.get(lang)[part.ordinal()]) + ". ";
      if (critHit) {
        tmp += "Critical hit! ";
      }
      tmp += "-" + damage + " [" + victim.hp + "/" + victim.getMaxHp() + "]";
      result.put(lang, tmp);
    }
    return result;
  }

  static Map<String, String> getBlockPhrase(Client offender,
                               Client victim,
                               Client.BodyPart part) {
    Map<String, String> result = new HashMap<>();
    for (String lang : Phrases.combatTexts.lookingForOpponent.keySet()) {
      result.put(lang, offender.username + " " + 
        Utils.getRnd(Phrases.combatTexts.wasTrying.get(lang)) + " " +
        Utils.getRnd(Phrases.combatTexts.toHit.get(lang)[part.ordinal()]) +
        " " + Utils.getRnd(Phrases.combatTexts.but.get(lang)) + " " +
        victim.username +  " " +
        Utils.getRnd(Phrases.combatTexts.blocked.get(lang)) + ". " +
        "[" + victim.hp + "/" + victim.getMaxHp() + "]");
    }
    return result;
  }

  static Map<String, String> getMissPhrase(Client offender,
                              Client victim,
                              Client.BodyPart part) {
    Map<String, String> result = new HashMap<>();
    for (String lang : Phrases.combatTexts.lookingForOpponent.keySet()) {
      result.put(lang, offender.username + " " +
        Utils.getRnd(Phrases.combatTexts.wasTrying.get(lang)) + " " +
        Utils.getRnd(Phrases.combatTexts.toHit.get(lang)[part.ordinal()]) +
        " " + Utils.getRnd(Phrases.combatTexts.but.get(lang)) +  " " +
        Utils.getRnd(Phrases.combatTexts.missed.get(lang)) + ". " +
        "[" + victim.hp + "/" + victim.getMaxHp() + "]");
    }
    return result;
  }


  static Map<String, String> getSayingPhrase(Client teller, String msg, Client listener) {
    Map<String, String> result = new HashMap<>();
    for (String lang : Phrases.combatTexts.lookingForOpponent.keySet()) {
      result.put(lang, teller.username + " " +
        Utils.getRnd(Phrases.combatTexts.said.get(lang)) + " " +
        listener.username + ": " + msg);
    }
    return result;
  }
};

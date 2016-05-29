package ChatBot;

class Tangram {
  class GetUpdatesResult {
    boolean ok;
    Update[] result;
  }

  class Update {
    int update_id;
    Message message;
  }

  class Message {
    int message_id;
    int date;
    String text;
    User from;
    Chat chat;
  }

  class User {
    int id;
    String first_name;
    String last_name;
  }

  class Chat {
    int id;
    String first_name;
    String last_name;
    String type;
  }
}

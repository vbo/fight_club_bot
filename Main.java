package ChatBot;

public class Main {
  public static void main(String[] args) {
    Telegram.Update[] updates = TelegramApi.getUpdates();
    for (Telegram.Update upd : updates) {
      int chatId = upd.message.chat.id;
      TelegramApi.sayHi(chatId);
    }
  }
}

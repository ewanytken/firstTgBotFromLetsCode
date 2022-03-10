package demo.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.SendResponse;
import com.sun.xml.internal.ws.resources.SenderMessages;

public class Bot {
    TelegramBot bot = new TelegramBot(System.getenv("Token"));

    public void server() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();
        BaseRequest request = null;

        if(message != null){
            long chatId = message.chat().id();
            request = new SenderMessages(chatId, "hello");
        }
        if(request != null){
            bot.execute(request);
        }

    }
}

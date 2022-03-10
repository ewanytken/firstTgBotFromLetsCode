package demo.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot {

    private final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final String PROCESSING = "Processing...";
    TelegramBot bot = new TelegramBot(System.getenv("BOT_TOKEN"));

    public void server() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();
        InlineQuery inlineQuery = update.inlineQuery();
        BaseRequest request = null;

        if(message != null && message.viaBot() != null
                && message.viaBot().username()
                    .equals(System.getenv("BOT_NAME"))){

            InlineKeyboardMarkup replyMarkup = message.replyMarkup();
            InlineKeyboardButton[][] buttons = replyMarkup.inlineKeyboard();
            String senderChose = buttons[0][0].text();

            if(replyMarkup == null){
                return;
            }
            if(buttons == null){
                return;
            }
            if(!senderChose.equals(PROCESSING)){
                return;
            }
            Long chatId = message.chat().id();
            String senderName = message.from().firstName();
            Integer messageId = message.messageId();

            request = new EditMessageText(chatId, messageId, message.text())
                    .replyMarkup(
                            new InlineKeyboardMarkup(
                                    new InlineKeyboardButton("\uD83E\uDEA8")
                                            .callbackData(String.format("%d %s %s %s,",
                                                    chatId, senderName, senderChose, "0")),

                                    new InlineKeyboardButton("✂️")
                                            .callbackData(String.format("%d %s %s %s,",
                                                    chatId, senderName, senderChose, "1")),

                                    new InlineKeyboardButton("\uD83D\uDDDE")
                                            .callbackData(String.format("%d %s %s %s,",
                                                    chatId, senderName, senderChose, "2"))
                            )
                    );

        } else if(inlineQuery != null){
            InlineQueryResultArticle stone =
                    getInline("stone", "\uD83E\uDEA8 Stone", "0");

            InlineQueryResultArticle scissors =
                    getInline("scissors", "✂️Scissors", "1");

            InlineQueryResultArticle paper =
                    getInline("paper", "\uD83D\uDDDE Paper", "2");

            request = new AnswerInlineQuery(inlineQuery.id(), stone, scissors, paper).cacheTime(1);

            if (callbackQuery == null) {
                logger.debug("callbackQuery is null");
            }
        }/* else if(message != null){
            long chatId = message.chat().id();
            request = new SendMessage(chatId, "I am only your bot");
        }*/
        if(request != null){
            bot.execute(request);
        }

    }

    private InlineQueryResultArticle getInline(String id, String title, String callbackData) {
        return new InlineQueryResultArticle(id, title, "I m ready to fight")
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton(PROCESSING)
                            .callbackData(callbackData)
                ));
    }
}

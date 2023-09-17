package kz.qBots.fisrtBot.service;

import kz.qBots.fisrtBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig botConfig;
    public TelegramBot(BotConfig botConfig) {
        this.botConfig=botConfig;
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()&&update.getMessage().hasText()){
            String messageText=update.getMessage().getText();
            long chatId=update.getMessage().getChatId();
            switch (messageText){
                case "/start":
                    startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
                    break;
                default:
                    sendMessage(chatId,"Sorry, command was not realize");
            }
        }
    }
    private void startCommandReceived(long chatId,String firstName){
        String answer="Hi, "+firstName+", nice to meet you!";
        log.info("Replied to user"+firstName);
        sendMessage(chatId,answer);
    }
    private void sendMessage(long chatId,String textToSend){
        SendMessage message=new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        }catch (TelegramApiException e){
            log.error("Error occurred: "+e.getMessage());
        }
    }
    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}

package kz.qBots.fisrtBot.service;

import com.vdurmont.emoji.EmojiParser;
import kz.qBots.fisrtBot.config.BotConfig;
import kz.qBots.fisrtBot.model.User;
import kz.qBots.fisrtBot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    final BotConfig botConfig;
    static final String HELP_TEXT="This bot is created to demonstrate String capabilities.\n\n"+
            "You can execute commands from the main menu on the left or by typing a command:\n\n"+
            "Type /start to see a welcome message\n\n"+
            "Type /mydata to see a data store about yourself\n\n"+
            "Type /help to see this message again";
    public TelegramBot(BotConfig botConfig) {
        this.botConfig=botConfig;
        List<BotCommand> listOfCommands=new ArrayList();
        listOfCommands.add(new BotCommand("/start","get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata","get my data store"));
        listOfCommands.add(new BotCommand("/deletedata","delete my data"));
        listOfCommands.add(new BotCommand("/help","info how use this bot"));
        listOfCommands.add(new BotCommand("/settings","set my preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands,new BotCommandScopeDefault(),null));
        }catch (TelegramApiException e){
            log.error("Error setting bot's command list: "+e.getMessage());
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()&&update.getMessage().hasText()){
            String messageText=update.getMessage().getText();
            long chatId=update.getMessage().getChatId();
            switch (messageText){
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId,HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId,"Sorry, command was not realize");
            }
        }
    }
    private void startCommandReceived(long chatId,String firstName){
        String answer= EmojiParser.parseToUnicode("Hi, "+firstName+", nice to meet you!"+" :blush:");
        log.info("Replied to user"+firstName);
        sendMessage(chatId,answer);
    }
    private void sendMessage(long chatId,String textToSend){
        SendMessage message=new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(setKeyboard());
        try {
            execute(message);
        }catch (TelegramApiException e){
            log.error("Error occurred: "+e.getMessage());
        }
    }
    private ReplyKeyboardMarkup setKeyboard(){
        ReplyKeyboardMarkup keyboardMarkup=new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows=new ArrayList<>();
        KeyboardRow row=new KeyboardRow();
        row.add("weather");
        row.add("get random joke");
        keyboardRows.add(row);
        row=new KeyboardRow();
        row.add("register");
        row.add("check my data");
        row.add("delete my data");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
    private void registerUser(Message message){
        if(userRepository.findById(message.getChatId()).isEmpty()){
            Long charId=message.getChatId();
            Chat chat=message.getChat();
            User user=new User();
            user.setChatId(charId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("user saved: "+user);
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

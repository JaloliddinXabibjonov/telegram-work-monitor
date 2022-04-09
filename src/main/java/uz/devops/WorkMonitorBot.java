package uz.devops;

import static uz.devops.domain.enumeration.Command.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.devops.command.CommandContainer;
import uz.devops.utils.BotUtils;

@Component
public class WorkMonitorBot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "devopsmonitor_bot";
    private static final String BOT_TOKEN = "5131660890:AAHv3lYFOwGrIJMYvteMMQzBDcr8o1AlGuU";
    public static final String ADMIN_1_CHAT_ID = "910061782";

    @Lazy
    @Autowired
    private CommandContainer commandContainer;

    @Autowired
    private BotUtils botUtils;

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery()) {
            sendMessage(update);
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            onMessageReceived(update);
        }

        if (update.hasMessage() && update.getMessage().hasContact()) {
            shareContact(update);
        }

        if (update.hasCallbackQuery()) {
            onCallbackReceived(update);
        }
    }

    private void onMessageReceived(Update update) {
        botUtils
            .findByChatId(update.getMessage().getChatId())
            .ifPresent(user -> {
                if (user.getState() != null) {
                    commandContainer.stateProcessor(user.getState()).execute(update);
                } else {
                    commandContainer.mainProcessor(update.getMessage().getText()).execute(update);
                }
            });
        if (botUtils.userIsEmpty(update.getMessage().getChatId())) {
            commandContainer.registrationProcessor(update).execute(update);
        }
    }

    private void shareContact(Update update) {
        commandContainer.shareContact(SHARE_CONTACT.getCommandName()).execute(update);
    }

    private void onCallbackReceived(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = callbackQuery.getMessage();
        String data = callbackQuery.getData();

        botUtils
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (user.getState() != null) {
                    commandContainer.stateProcessor(user.getState()).execute(update);
                }
            });
        if (data.equals(CONFIRM_USER_PROFESSION.getCommandName())) {
            commandContainer.mainProcessor(data).execute(update);
        } else if (message.getText().startsWith(ASSIGN_ROLE_TO_USER.getCommandName())) {
            commandContainer.setRoleToUser().execute(update);
        } else if (data.equals(CONFIRM_TASK_PROFESSION.getCommandName())) {
            commandContainer.mainProcessor(data).execute(update);
        } else if (message.getText().startsWith(ASSIGN_ROLE_TO_TASK.getCommandName())) {
            commandContainer.setRoleToTask().execute(update);
        } else {
            commandContainer.mainProcessor(data).execute(update);
        }
    }

    private void sendMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText("Noto'gri xabar kiritildi !!!");
        sendMessage.setParseMode(ParseMode.HTML);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

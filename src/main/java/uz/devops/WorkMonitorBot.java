package uz.devops;

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
import uz.devops.config.ApplicationProperties;
import uz.devops.utils.BotUtils;

import static uz.devops.domain.enumeration.Command.*;

@Component
public class WorkMonitorBot extends TelegramLongPollingBot {

    private final ApplicationProperties applicationProperties;
    public static final String ADMIN_1_CHAT_ID = "910061782";

    @Lazy
    @Autowired
    private CommandContainer commandContainer;

    @Autowired
    private BotUtils botUtils;

    public WorkMonitorBot(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public String getBotUsername() {
        return applicationProperties.getBot().getName();
    }

    @Override
    public String getBotToken() {
        return applicationProperties.getBot().getToken();
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
                if (user.getState() == null) {
                    commandContainer.mainProcessor(update.getMessage().getText()).execute(update);
                }
                commandContainer.stateProcessor(user.getState()).execute(update);
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

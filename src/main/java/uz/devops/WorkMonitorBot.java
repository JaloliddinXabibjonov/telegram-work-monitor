package uz.devops;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.CommandContainer;
import uz.devops.command.Processor;
import uz.devops.command.other.UnknownCommand;
import uz.devops.config.ApplicationProperties;
import uz.devops.config.Constants;
import uz.devops.domain.Button;
import uz.devops.domain.User;
import uz.devops.repository.ButtonRepository;
import uz.devops.utils.BotUtils;

import java.util.Optional;

import static uz.devops.domain.enumeration.Command.*;

@Slf4j
@Component
public class WorkMonitorBot extends TelegramLongPollingBot {

    private final ApplicationProperties applicationProperties;
    public static final String ADMIN_1_CHAT_ID = "573492532";

    @Lazy
    @Autowired
    private CommandContainer commandContainer;

    @Autowired
    private UnknownCommand unknownCommand;

    @Autowired
    private ButtonRepository buttonRepository;
    @Autowired
    private BotUtils botUtils;

    @Autowired
    private ApplicationContext applicationContext;

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
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                onMessageReceived(update);
            } else if (update.getMessage().hasContact()) {
                shareContact(update);
            }
        } else if (update.hasCallbackQuery()) {
            onCallbackReceived(update);
        } else {
            unknownCommand.execute(update);
        }
    }

    private void onMessageReceived(Update update) {
        Optional<User> optionalUser = botUtils.findByChatId(update.getMessage().getChatId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getState() == null) {
                String text = update.getMessage().getText();
                Optional<Button> optionalButton = buttonRepository.findByName(text);
                if (optionalButton.isPresent()) {
                    String command = optionalButton.get().getName();
                    if (applicationContext.containsBeanDefinition(command)) {
                        try {
                            final Processor bean = applicationContext.getBean(command, Processor.class);
                            bean.execute(update);
                        } catch (Exception e) {
                            log.warn("Error on searching command service : " + e.getLocalizedMessage());
                        }
                    }
                } else if (text.startsWith(ADD_ORDER_BY_SHORT_COMMAND.getCommandName())) {
                    commandContainer.callbackDataProcessor(ADD_ORDER_BY_SHORT_COMMAND.getCommandName()).execute(update);
                }
            } else {
                commandContainer.stateProcessor(user.getState()).execute(update);
            }
        } else {
            commandContainer.registrationProcessor(update).execute(update);
        }
    }

    private void shareContact(Update update) {
        commandContainer.shareContact(SHARE_CONTACT.getCommandName()).execute(update);
    }

    private void onCallbackReceived(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        String command;
        if (data.startsWith(SET_PROFESSION_TO_TASK.getCommandName())) {
            command=SET_PROFESSION_TO_TASK.getCommandName();
        } else if (data.startsWith(Constants.CHOOSE_ROLE)) {
            command= SET_ROLE_TO_USER.getCommandName();
        } else if (data.startsWith(SET_PROFESSION_TO_USER.getCommandName())) {
            command=SET_PROFESSION_TO_USER.getCommandName();
        } else if (data.startsWith(CONFIRM_TASK_PROFESSION.getCommandName())) {
            command=CONFIRM_TASK_PROFESSION.getCommandName();
        } else if (data.startsWith(CONFIRM_ORDER.getCommandName())) {
            command=CONFIRM_ORDER.getCommandName();
        } else if (data.startsWith(TASK_DONE.getCommandName())) {
            command=TASK_DONE.getCommandName();
        } else if (data.startsWith(REJECT_ORDER.getCommandName())) {
            command=REJECT_ORDER.getCommandName();
        } else if (data.startsWith(CONFIRM_PROFESSION.getCommandName())) {
            command=CONFIRM_PROFESSION.getCommandName();
        } else if (data.startsWith(GET_PAGE_OF_MY_DONE_TASKS.getCommandName())) {
            command=GET_PAGE_OF_MY_DONE_TASKS.getCommandName();
        } else if (data.startsWith(Constants.GET_ORDER_TASK)) {
            command=Constants.GET_ORDER_TASK;
        } else if (data.startsWith(Constants.ADD_TASK_TO_JOB)) {
            command=Constants.ADD_TASK_TO_JOB;
        } else if (data.startsWith(GET_MAIN_KEYBOARDS.getCommandName())) {
            command=Constants.GET_MAIN_KEYBOARDS;
        }else if (data.startsWith(Constants.GET_ORDER_TASK_BY_ID)) {
            command=Constants.GET_ORDER_TASK_BY_ID;
        }else if (data.startsWith(Constants.ADD_NEW_PROFESSION)) {
            command=Constants.ADD_NEW_PROFESSION;
        } else if (data.startsWith(Constants.REMOVE_PROFESSION_COMMAND)) {
            command=Constants.REMOVE_PROFESSION_COMMAND;
        }else if (data.startsWith(Constants.REMOVE_PROFESSION_BY_NAME)) {
            command=Constants.REMOVE_PROFESSION_BY_NAME;
        } else {
            command=data;
        }

        if (applicationContext.containsBeanDefinition(command)) {
            try {
                final Processor bean = applicationContext.getBean(command, Processor.class);
                bean.execute(update);
            } catch (Exception e) {
                log.warn("Error on searching command service : " + e.getLocalizedMessage());
            }
        }

    }


}

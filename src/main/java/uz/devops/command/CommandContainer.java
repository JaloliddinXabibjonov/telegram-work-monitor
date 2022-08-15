package uz.devops.command;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.job.*;
import uz.devops.command.order.*;
import uz.devops.command.other.*;
import uz.devops.command.task.*;
import uz.devops.command.task.edit.*;
import uz.devops.command.user.*;
import uz.devops.domain.enumeration.BotState;

import javax.annotation.PostConstruct;

import static uz.devops.domain.enumeration.Command.*;

@RequiredArgsConstructor
@Service
public class CommandContainer {

    private ImmutableMap<String, Processor> callBackDataCommandMap;
    private ImmutableMap<String, Processor> registrationMap;
    private ImmutableMap<BotState, Processor> stateProcessorMap;

    private final UnknownCommand unknownCommand;
    private final StartCommand startCommand;
    private final ShareContactCommand shareContactCommand;
    private final AboutBotCommand aboutBotCommand;
    private final GetContactFromUser getContactFromUser;
    private final EditJob editJob;
    private final RemoveJob removeJob;
    private final CreateJob createJob;
    private final CreateTask createTask;
    private final EditTask editTask;
    private final EnterOrderFields enterOrderFields;
    private final GetMainKeyboards getMainKeyboards;
    private final EditTaskName editTaskName;
    private final EditTaskPrice editTaskPrice;
    private final EditTaskViaState editTaskViaState;
    private final CreateOrder createOrder;
    private final EditJobName editJobName;
    @Autowired
    private SaveNewUserPhoneNumber saveNewUserPhoneNumber;
    @Autowired
    private SaveNewProfession saveNewProfession;
    @Autowired
    private CreateOrderByShortCommand createOrderByShortCommand;

    @PostConstruct
    public void init() {
        registrationMap =
            ImmutableMap
                .<String, Processor>builder()
                .put(START.getCommandName(), startCommand)
                .put(REGISTRATION.getCommandName(), shareContactCommand)
                .put(SHARE_CONTACT.getCommandName(), getContactFromUser)
                .put(ABOUT_BOT.getCommandName(), aboutBotCommand)
                .build();

        callBackDataCommandMap =
            ImmutableMap
                .<String, Processor>builder()
                .put(EDIT_JOB_NAME.getCommandName(), editJob)
                .put(REMOVE_JOB.getCommandName(), removeJob)
                .put(EDIT_TASK.getCommandName(), editTask)
                .put(EDIT_TASK_NAME.getCommandName(), editTask)
                .put(EDIT_TASK_PRICE.getCommandName(), editTask)
                .put(CREATE_ORDER.getCommandName(), enterOrderFields)
                .put(GET_MAIN_KEYBOARDS.getCommandName(), getMainKeyboards)
                .put(MENU.getCommandName(), getMainKeyboards)
                .put(ADD_ORDER_BY_SHORT_COMMAND.getCommandName(), createOrderByShortCommand)
                .build();

        stateProcessorMap =
            ImmutableMap
                .<BotState, Processor>builder()
                .put(BotState.ENTER_TASK_NAME, createTask)
                .put(BotState.ENTER_TASK_PRICE, createTask)
                .put(BotState.ENTER_TASK_DESCRIPTION, createTask)
                .put(BotState.ENTER_TASK_NEW_NAME, editTaskName)
                .put(BotState.ENTER_TASK_NEW_PRICE, editTaskPrice)
                .put(BotState.EDIT_TASK, editTaskViaState)
                .put(BotState.COMPLETED_CREATE_TASK, createTask)
                .put(BotState.ENTER_ORDER_COUNT, createOrder)
                .put(BotState.ENTER_JOB_NAME, createJob)
                .put(BotState.ENTER_JOB_NEW_NAME, editJobName)
                .put(BotState.ENTER_ORDER_DESCRIPTION, createOrder)
                .put(BotState.ENTER_NEW_USER_PHONE_NUMBER, saveNewUserPhoneNumber)
                .put(BotState.ENTER_NEW_PROFESSION_NAME, saveNewProfession)
                .build();
    }

    public Processor callbackDataProcessor(String command) {
        return callBackDataCommandMap.getOrDefault(command, unknownCommand);
    }

    public Processor stateProcessor(BotState botState) {
        return stateProcessorMap.get(botState);
    }

    public Processor registrationProcessor(Update update) {
        return registrationMap.getOrDefault(update.getMessage().getText(), unknownCommand);
    }

    public Processor shareContact(String shareContact) {
        return registrationMap.getOrDefault(shareContact, unknownCommand);
    }

}

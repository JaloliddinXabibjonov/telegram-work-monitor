package uz.devops.command;

import static uz.devops.domain.enumeration.Command.*;

import com.google.common.collect.ImmutableMap;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.job.*;
import uz.devops.command.order.*;
import uz.devops.command.other.*;
import uz.devops.command.task.*;
import uz.devops.command.task.edit.*;
import uz.devops.command.taskInfo.CreateTaskInfo;
import uz.devops.command.user.*;
import uz.devops.domain.enumeration.BotState;

@RequiredArgsConstructor
@Service
public class CommandContainer {

    private ImmutableMap<String, Processor> mainCommandMap;
    private ImmutableMap<String, Processor> registrationMap;
    private ImmutableMap<BotState, Processor> stateProcessorMap;

    private final UnknownCommand unknownCommand;
    private final StartCommand startCommand;
    private final ShareContactCommand shareContactCommand;
    private final AboutBotCommand aboutBotCommand;
    private final UserNotificationToAdmin userNotificationToAdmin;
    private final MyInfoCommand myInfoCommand;
    private final AssignRoleToUser assignRoleToUser;
    private final ConfirmUserProfession confirmUserProfession;
    private final RemoveUser removeUser;
    private final ReviewJob reviewJob;
    private final EditJob editJob;
    private final RemoveJob removeJob;
    private final CreateJob createJob;
    private final AddNewTaskToJob addNewTaskToJob;
    private final ChooseOneToEdit chooseOneToEdit;
    private final CreateTask createTask;
    private final RemoveTask removeTask;
    private final EditTask editTask;
    private final ChooseTaskToEdit chooseTaskToEdit;
    private final ConfirmTaskProfession confirmTaskProfession;
    private final CreateOrder createOrder;
    private final GetTaskInfo getTaskInfo;
    private final MyTasks myTasks;
    private final GetOrder getOrder;
    private final OrderDone orderDone;
    private final RejectOrder rejectOrder;
    private final ConfirmOrder confirmOrder;
    private final CallToAdmin callToAdmin;
    private final GetMainKeyboards getMainKeyboards;
    private final EditTaskName editTaskName;
    private final EditTaskPrice editTaskPrice;
    private final EditTaskViaState editTaskViaState;
    private final EditJobName editJobName;
    private final SetRoleToUser setRoleToUser;
    private final SetRoleToTask setRoleToTask;
    private final CreateTaskInfo createTaskInfo;
    private final CreateFullOrder createFullOrder;

    @PostConstruct
    public void init() {
        registrationMap =
            ImmutableMap
                .<String, Processor>builder()
                .put(START.getCommandName(), startCommand)
                .put(REGISTRATION.getCommandName(), shareContactCommand)
                .put(SHARE_CONTACT.getCommandName(), userNotificationToAdmin)
                .put(ABOUT_BOT.getCommandName(), aboutBotCommand)
                .build();

        mainCommandMap =
            ImmutableMap
                .<String, Processor>builder()
                .put(MY_INFO.getCommandName(), myInfoCommand)
                .put(SET_ROLE_TO_USER.getCommandName(), assignRoleToUser)
                .put(CONFIRM_USER_PROFESSION.getCommandName(), confirmUserProfession)
                .put(REMOVE_USER.getCommandName(), removeUser)
                .put(REVIEW_JOB.getCommandName(), reviewJob)
                .put(EDIT_JOB.getCommandName(), editJob)
                .put(EDIT_JOB_NAME.getCommandName(), editJob)
                .put(REMOVE_JOB.getCommandName(), removeJob)
                .put(CREATE_NEW_JOB.getCommandName(), createJob)
                .put(ADD_TASK_TO_JOB.getCommandName(), addNewTaskToJob)
                .put(CHOOSE_ONE_TO_EDIT.getCommandName(), chooseOneToEdit)
                .put(CREATE_NEW_TASK.getCommandName(), createTask)
                .put(REMOVE_TASK.getCommandName(), removeTask)
                .put(EDIT_TASK.getCommandName(), editTask)
                .put(EDIT_TASK_NAME.getCommandName(), editTask)
                .put(EDIT_TASK_PRICE.getCommandName(), editTask)
                .put(CHOOSE_TASK_TO_EDIT.getCommandName(), chooseTaskToEdit)
                .put(CONFIRM_TASK_PROFESSION.getCommandName(), confirmTaskProfession)
                .put(CREATE_NEW_ORDER.getCommandName(), createOrder)
                .put(CREATE_ORDER.getCommandName(), createFullOrder)
                .put(EXIST_TASKS.getCommandName(), getTaskInfo)
                .put(MY_TASKS.getCommandName(), myTasks)
                .put(GET_ORDER.getCommandName(), getOrder)
                .put(ORDER_DONE.getCommandName(), orderDone)
                .put(REJECT_ORDER.getCommandName(), rejectOrder)
                .put(CONFIRM_ORDER.getCommandName(), confirmOrder)
                .put(CALL_TO_ADMIN.getCommandName(), callToAdmin)
                .put(GET_MAIN_KEYBOARDS.getCommandName(), getMainKeyboards)
                .put(MENU.getCommandName(), getMainKeyboards)
                .build();

        stateProcessorMap =
            ImmutableMap
                .<BotState, Processor>builder()
                .put(BotState.ENTER_TASK_NAME, createTask)
                .put(BotState.ENTER_TASK_PRICE, createTaskInfo)
                .put(BotState.ENTER_TASK_DESCRIPTION, createTaskInfo)
                .put(BotState.ENTER_TASK_NEW_NAME, editTaskName)
                .put(BotState.ENTER_TASK_NEW_PRICE, editTaskPrice)
                .put(BotState.EDIT_TASK, editTaskViaState)
                .put(BotState.COMPLETED_CREATE_TASK, createTask)
                .put(BotState.ENTER_JOB_NAME, createJob)
                .put(BotState.ENTER_JOB_NEW_NAME, editJobName)
                .build();
    }

    public Processor mainProcessor(String command) {
        return mainCommandMap.getOrDefault(command, unknownCommand);
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

    public Processor setRoleToUser() {
        return setRoleToUser;
    }

    public Processor setRoleToTask() {
        return setRoleToTask;
    }
}

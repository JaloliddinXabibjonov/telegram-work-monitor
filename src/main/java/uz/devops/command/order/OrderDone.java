package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.utils.MessageUtils.CREATED_NEW_TASK;
import static uz.devops.utils.MessageUtils.SERVER_ERROR;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.UserRepository;
import uz.devops.service.*;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class OrderDone implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderTaskService orderTaskService;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;
    private final TaskService taskService;
    private final UserService userService;
    private final JobService jobService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long orderTaskId = botUtils.getOrderTaskId(message.getText());
        Long taskId = botUtils.getTaskIdFromText(message.getText());

        var completedOrderTask = orderTaskService.completedOrderTask(orderTaskId);
        var orderTask = completedOrderTask.getData();
        if (completedOrderTask.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }

        var taskById = taskService.findTaskById(orderTask.getTask().getId());
        messageSenderService.sendEditMessage(
            message.getChatId(),
            messageUtils.getAboutMyTask(orderTask, taskById.getData()),
            message.getMessageId(),
            null
        );
        messageSenderService.sendMessageForAdmin(
            List.of(ADMIN_1_CHAT_ID),
            messageUtils.taskDoneForAdmin(orderTask.getOrder().getId(), orderTask.getEmployeeUsername()),
            null
        );

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setBusy(false);
                userRepository.save(user);
            });

        // -- AUTO CREATE ORDER

        var jobByOrderId = jobService.findJobByOrderId(completedOrderTask.getData().getOrder().getId());
        var job = jobByOrderId.getData();
        var nextTask = taskService.findNextTask(job.getId(), taskId);

        if (Objects.equals(Boolean.FALSE, nextTask.getSuccess())) {
            messageSenderService.sendMessage(message.getChatId(), messageUtils.jobCompleted(job.getName()), null);
            return;
        }

        var availableTask = nextTask.getData();
        var resultData = orderTaskService.createOrderTask(orderTask.getOrder().getId(), availableTask.getId());

        if (resultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }

        var simpleResultData = userService.findAllAvailableUsers(availableTask.getId());
        if (simpleResultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessageForAdmin(
                List.of(ADMIN_1_CHAT_ID),
                CREATED_NEW_TASK,
                botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
            );
            return;
        }

        var newOrderTask = resultData.getData();
        var availableUsers = simpleResultData.getData();
        availableUsers.forEach(user ->
            messageSenderService.sendMessage(
                Long.valueOf(user.getChatId()),
                messageUtils.getTaskInfo(newOrderTask.getId(), newOrderTask.getOrder(), availableTask),
                BotUtils.getOrderKeyboard()
            )
        );

        messageSenderService.sendMessageForAdmin(
            List.of(ADMIN_1_CHAT_ID),
            CREATED_NEW_TASK,
            botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
        );
    }
}

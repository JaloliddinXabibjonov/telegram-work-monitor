package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.utils.MessageUtils.SERVER_ERROR;
import static uz.devops.utils.MessageUtils.WORK_GOT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.OrderTaskService;
import uz.devops.service.TaskService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class GetOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderTaskService orderTaskService;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;
    private final TaskService taskService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long orderTaskId = botUtils.getOrderTaskId(message.getText());
        Long taskId = botUtils.getTaskIdFromText(message.getText());

        var simpleResultData = orderTaskService.findById(orderTaskId);
        if (simpleResultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }

        var orderTask = simpleResultData.getData();
        var orderSimpleResultData = orderTaskService.checkOrderStatus(orderTask.getOrder().getId(), taskId);
        if (orderSimpleResultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
            messageSenderService.sendMessage(message.getChatId(), WORK_GOT, null);
            return;
        }

        var resultData = orderTaskService.startedOrderTask(
            orderTaskId,
            message.getChat().getUserName(),
            orderTask.getOrder().getId(),
            taskId
        );
        if (resultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
        }

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setBusy(true);
                userRepository.save(user);
            });

        var taskById = taskService.findTaskById(taskId);
        var startedOrderTask = resultData.getData();

        messageSenderService.sendEditMessage(
            message.getChatId(),
            messageUtils.myTask(taskById.getData(), startedOrderTask),
            message.getMessageId(),
            botUtils.getTaskOrderKeyboard()
        );
        messageSenderService.sendMessageForAdmin(
            List.of(ADMIN_1_CHAT_ID),
            messageUtils.getTaskInfoAfterTook(taskById.getData(), startedOrderTask),
            null
        );
    }
}

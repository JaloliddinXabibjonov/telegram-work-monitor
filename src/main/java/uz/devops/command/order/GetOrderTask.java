package uz.devops.command.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.User;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.OrderTaskService;
import uz.devops.service.TaskService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.util.Optional;

import static uz.devops.utils.MessageUtils.*;

@Service(Constants.GET_ORDER_TASK)
@RequiredArgsConstructor
public class GetOrderTask implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderTaskService orderTaskService;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;
    private final TaskService taskService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long orderTaskId = Long.valueOf(update.getCallbackQuery().getData().split("#")[1]);
        Long taskId = botUtils.getTaskIdFromText(message.getText());

        Optional<User> optionalUser = userRepository.findByChatId(message.getChatId().toString());
        if (optionalUser.isEmpty()){
            messageSenderService.sendMessage(message.getChatId(), USER_NOT_FOUND, null);
            return;
        }
        Boolean busy = optionalUser.get().getBusy();
        if (busy){
            messageSenderService.sendMessageWithReply(message.getChatId().toString(), YOU_CANNOT_GET_ORDER_TASK , message.getMessageId());
            return;
        }
        var simpleResultData = orderTaskService.findById(orderTaskId);
        if (simpleResultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }

        var orderTask = simpleResultData.getData();
        var orderSimpleResultData = orderTaskService.checkOrderStatus(orderTask.getId());
        if (orderSimpleResultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
            messageSenderService.sendMessage(message.getChatId(), WORK_GOT, null);
            return;
        }

        var resultData = orderTaskService.startedOrderTask(
            orderTaskId,
            message.getChat().getUserName(),
            orderTask.getOrder().getId(),
            orderTask.getTask().getId()
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
            botUtils.getTaskOrderKeyboardByOrderTaskId(startedOrderTask.getId())
        );
    }
}

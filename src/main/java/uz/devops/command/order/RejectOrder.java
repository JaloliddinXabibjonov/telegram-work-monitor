package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.utils.MessageUtils.SERVER_ERROR;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.OrderTaskService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class RejectOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderTaskService orderTaskService;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long orderTaskId = botUtils.getAnyIdFromText(message.getText());

        var resultData = orderTaskService.findById(orderTaskId);
        if (resultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }

        var simpleResultData = orderTaskService.rejectOrderTask(orderTaskId);
        if (simpleResultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }

        var orderTask = simpleResultData.getData();
        var order = orderTask.getOrder();
        var task = orderTask.getTask();
        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setBusy(false);
                userRepository.save(user);
            });

        messageSenderService.sendEditMessage(
            message.getChatId(),
            messageUtils.getTaskInfo(orderTaskId, order, task),
            message.getMessageId(),
            BotUtils.getOrderKeyboard()
        );
        messageSenderService.sendMessageForAdmin(List.of(ADMIN_1_CHAT_ID), messageUtils.rejectTask(order, task, message), null);
    }
}

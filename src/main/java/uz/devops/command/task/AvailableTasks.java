package uz.devops.command.task;

import static uz.devops.utils.MessageUtils.NO_WORKS_YET;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.OrderTask;
import uz.devops.service.MessageSenderService;
import uz.devops.service.OrderTaskService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class AvailableTasks implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderTaskService orderTaskService;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();

        var availableOrderTasks = orderTaskService.findAvailableOrderTask(chatId);

        if (availableOrderTasks.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(update.getMessage().getChatId(), NO_WORKS_YET, null);
            return;
        }

        var orderTasks = availableOrderTasks.getData();
        for (OrderTask orderTask : orderTasks) {
            messageSenderService.sendMessage(
                update.getMessage().getChatId(),
                messageUtils.getTaskInfo(orderTask.getId(), orderTask.getOrder(), orderTask.getTask()),
                BotUtils.getOrderKeyboard()
            );
        }
    }
}

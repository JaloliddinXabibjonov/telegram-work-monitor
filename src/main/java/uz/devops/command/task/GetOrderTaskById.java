package uz.devops.command.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.command.ResultData;
import uz.devops.config.Constants;
import uz.devops.domain.OrderTask;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.util.Optional;

import static uz.devops.utils.MessageUtils.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service(Constants.GET_ORDER_TASK_BY_ID)
public class GetOrderTaskById implements Processor {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private OrderTaskRepository orderTaskRepository;
    @Autowired
    private MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        Long orderTaskId = Long.valueOf(update.getCallbackQuery().getData().split("#")[1]);
        Optional<OrderTask> orderTasks = orderTaskRepository.findById(orderTaskId);
        if (orderTasks.isEmpty()) {
            messageSenderService.answerCallbackQuery(update.getCallbackQuery().getId(), NOT_FOUND);
            return;
        }
        OrderTask orderTask = orderTasks.get();
        String text = messageUtils.getOrderTaskInfo(orderTask,orderTask.getOrder(),orderTask.getTask());
        messageSenderService.deleteMessage(message.getMessageId(), chatId.toString());
        messageSenderService.sendMessage(chatId, text, null);
    }

}

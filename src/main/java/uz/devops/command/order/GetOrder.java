package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.domain.enumeration.Command.ORDER_DONE;
import static uz.devops.domain.enumeration.Command.REJECT_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.MessageUtils.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Order;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.TaskService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class GetOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;
    private final TaskService taskService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long orderId = botUtils.getOrderIdFromText(message.getText());
        Long taskInfoId = botUtils.getTaskInfoIdFromText(message.getText());
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            log.info("Order not found");
            return;
        }

        Order order = optionalOrder.get();

        if (order.getChatId() != null) {
            messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
            messageSenderService.sendMessage(message.getChatId(), WORK_GOT, null);
            return;
        }

        order.setChatId(String.valueOf(message.getChatId()));
        order.setStatus(Status.DOING);
        order.setEmployee(message.getChat().getUserName());
        order.setStartedDate(Instant.now());
        orderRepository.save(order);

        taskService.changeTaskStatus(taskInfoId);
        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setBusy(true);
                userRepository.save(user);
            });

        var markupInline = newInlineKeyboardMarkup(
            List.of(
                newInlineKeyboardButton(TASK_DONE, ORDER_DONE.getCommandName()),
                newInlineKeyboardButton(TASK_REJECTED, REJECT_ORDER.getCommandName())
            )
        );

        messageSenderService.sendEditMessage(
            message.getChatId(),
            messageUtils.testTaskInfo(order, taskService.findTaskInfoById(taskInfoId)),
            message.getMessageId(),
            markupInline
        );
        messageSenderService.sendMessageForAdmin(
            List.of(ADMIN_1_CHAT_ID),
            messageUtils.getTaskInfoAfterTook(order, taskService.findTaskInfoById(taskInfoId)),
            null
        );
    }
}

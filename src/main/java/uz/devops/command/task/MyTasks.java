package uz.devops.command.task;

import static uz.devops.domain.enumeration.Command.ORDER_DONE;
import static uz.devops.domain.enumeration.Command.REJECT_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.MessageUtils.*;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Order;
import uz.devops.domain.enumeration.OrderStatus;
import uz.devops.repository.OrderRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class MyTasks implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderRepository orderRepository;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        List<Order> orders = orderRepository.findAllByChatIdAndStatusIsNotLike(String.valueOf(message.getChatId()), OrderStatus.TO_DO);

        if (orders.isEmpty()) {
            log.debug("Order not found with chatId: {} and status: {}", message.getChatId(), OrderStatus.DOING);
            messageSenderService.sendMessage(message.getChatId(), USER_WORK_NOT_FOUND, null);
            return;
        }

        var markupInline = newInlineKeyboardMarkup(
            List.of(
                newInlineKeyboardButton(TASK_DONE, ORDER_DONE.getCommandName()),
                newInlineKeyboardButton(TASK_REJECTED, REJECT_ORDER.getCommandName())
            )
        );

        orders
            .stream()
            .filter(order -> order.getStatus().equals(OrderStatus.DONE))
            .collect(Collectors.toList())
            .forEach(order -> messageSenderService.sendMessage(message.getChatId(), messageUtils.getAboutMyTask(order), null));

        orders
            .stream()
            .filter(order -> order.getStatus().equals(OrderStatus.DOING))
            .forEach(order -> messageSenderService.sendMessage(message.getChatId(), messageUtils.testTaskInfo(order), markupInline));
    }
}

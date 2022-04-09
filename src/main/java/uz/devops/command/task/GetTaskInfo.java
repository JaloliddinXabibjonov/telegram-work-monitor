package uz.devops.command.task;

import static uz.devops.domain.enumeration.Command.GET_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.EMPLOYEE_MAN_ICON;
import static uz.devops.utils.MessageUtils.NO_WORKS_YET;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class GetTaskInfo implements Processor {

    private final MessageSenderService messageSenderService;
    private final MessageUtils messageUtils;
    private final OrderRepository orderRepository;

    @Override
    public void execute(Update update) {
        List<Order> orders = orderRepository.findAllByStatus(OrderStatus.TO_DO);

        if (orders.isEmpty()) {
            log.debug("Order not found with status: {}", OrderStatus.TO_DO);
            messageSenderService.sendMessage(update.getMessage().getChatId(), NO_WORKS_YET, null);
            return;
        }

        var inlineKeyboardMarkup = newInlineKeyboardMarkup(
            List.of(newInlineKeyboardButton(GET_ORDER.getCommandName() + EMPLOYEE_MAN_ICON, GET_ORDER.getCommandName()))
        );

        orders.forEach(order ->
            messageSenderService.sendMessage(update.getMessage().getChatId(), messageUtils.getTaskInfo(order), inlineKeyboardMarkup)
        );
    }
}

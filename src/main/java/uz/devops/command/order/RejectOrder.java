package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.domain.enumeration.Command.GET_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.EMPLOYEE_MAN_ICON;

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
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class RejectOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BotUtils botUtils;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long parsedOrderId = botUtils.getAnyIdFromText(message.getText());
        Optional<Order> optionalOrder = orderRepository.findById(parsedOrderId);

        if (optionalOrder.isEmpty()) {
            log.info("Order not found with id: {}", parsedOrderId);
            return;
        }

        Order order = optionalOrder.get();
        order.setStatus(Status.TO_DO);
        order.setEmployee(null);
        order.setStartedDate(null);
        order.setChatId(null);
        orderRepository.save(order);

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setBusy(false);
                userRepository.save(user);
            });

        var inlineKeyboardMarkup = newInlineKeyboardMarkup(
            List.of(newInlineKeyboardButton(GET_ORDER.getCommandName() + EMPLOYEE_MAN_ICON, GET_ORDER.getCommandName()))
        );

        messageSenderService.sendEditMessage(
            message.getChatId(),
            messageUtils.getTaskInfo(order),
            message.getMessageId(),
            inlineKeyboardMarkup
        );
        messageSenderService.sendMessageForAdmin(List.of(ADMIN_1_CHAT_ID), messageUtils.rejectTask(order, message), null);
    }
}

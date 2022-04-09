package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.OrderStatus;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.OrderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderDone implements Processor {

    private final MessageSenderService messageSenderService;
    private final BotUtils botUtils;
    private final MessageUtils messageUtils;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long orderId = botUtils.getAnyIdFromText(message.getText());

        orderService
            .findOrderById(orderId)
            .ifPresent(order -> {
                order.setStatus(OrderStatus.DONE);
                order.setEndDate(Instant.now());
                orderRepository.save(order);

                messageSenderService.sendEditMessage(message.getChatId(), messageUtils.getAboutMyTask(order), message.getMessageId(), null);
                messageSenderService.sendMessageForAdmin(List.of(ADMIN_1_CHAT_ID), messageUtils.taskDoneForAdmin(order), null);
            });
        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setBusy(false);
                userRepository.save(user);
            });
    }
}

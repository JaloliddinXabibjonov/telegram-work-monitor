package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.domain.enumeration.Command.GET_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.EMPLOYEE_MAN_ICON;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.User;
import uz.devops.repository.OrderRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.OrderService;
import uz.devops.service.TaskService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConfirmOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final TaskService taskService;
    private final BotUtils botUtils;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long taskId = botUtils.getAnyIdFromText(message.getText());
        Long orderId = botUtils.getOrderIdFromText(message.getText());
        Long taskInfoId = botUtils.getTaskInfoIdFromText(message.getText());
        List<User> availableUsers = userService.checkAvailableUsers(taskId);

        orderService.addOrderToTaskInfo(orderId, taskInfoId);

        if (availableUsers.isEmpty()) {
            log.info("Available users not found");
            messageSenderService.sendMessageForAdmin(
                List.of(ADMIN_1_CHAT_ID),
                "Yangi buyurtma  #" + taskId + " yaratildi   ⚡",
                botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
            );
            return;
        }

        var inlineKeyboardMarkup = newInlineKeyboardMarkup(
            List.of(newInlineKeyboardButton(GET_ORDER.getCommandName() + EMPLOYEE_MAN_ICON, GET_ORDER.getCommandName()))
        );

        orderRepository
            .findById(orderId)
            .ifPresent(order ->
                availableUsers.forEach(user ->
                    messageSenderService.sendMessage(
                        Long.valueOf(user.getChatId()),
                        "Vazifa  #" + taskId + "\n" + messageUtils.getTaskInfo(order, taskService.getTaskInfo(order.getId())),
                        inlineKeyboardMarkup
                    )
                )
            );

        messageSenderService.sendMessageForAdmin(
            List.of(ADMIN_1_CHAT_ID),
            "Yangi buyurtma  #" + orderId + " yaratildi   ⚡",
            botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
        );
        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
    }
}

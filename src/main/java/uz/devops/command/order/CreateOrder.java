package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.utils.MessageUtils.ONLY_NUMBER;
import static uz.devops.utils.MessageUtils.SERVER_ERROR;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.*;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class CreateOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderTaskService orderTaskService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;
    private final OrderService orderService;
    private final UserService userService;
    private final TaskService taskService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (user.getState().equals(BotState.ENTER_ORDER_DESCRIPTION)) {
                    var description = update.getMessage().getText();
                    var orderSimpleResultData = orderService.createOrder(user.getTempTableId(), description);
                    if (orderSimpleResultData.getSuccess().equals(Boolean.FALSE)) {
                        messageSenderService.sendMessage(update.getMessage().getChatId(), SERVER_ERROR, null);
                        return;
                    }

                    user.setExtraTableId(orderSimpleResultData.getData().getId());
                    user.setState(BotState.ENTER_ORDER_COUNT);
                    userRepository.save(user);
                    messageSenderService.sendMessage(update.getMessage().getChatId(), messageUtils.enterOrderCount(), null);
                } else if (user.getState().equals(BotState.ENTER_ORDER_COUNT)) {
                    var count = update.getMessage().getText();
                    if (Objects.equals(botUtils.checkNumberValidation(update.getMessage().getText()), Boolean.FALSE)) {
                        messageSenderService.sendMessage(update.getMessage().getChatId(), ONLY_NUMBER, null);
                        return;
                    }

                    var orderById = orderService.findOrderById(user.getExtraTableId());
                    if (orderById.getSuccess().equals(Boolean.FALSE)) {
                        messageSenderService.sendMessage(update.getMessage().getChatId(), SERVER_ERROR, null);
                        return;
                    }

                    var order = orderById.getData();
                    order.setCount(Integer.valueOf(count));
                    orderRepository.save(order);

                    user.setState(null);
                    userRepository.save(user);
                    createOrder(user, message);
                }
            });
    }

    private void createOrder(User user, Message message) {
        var firstTaskByJobId = taskService.findFirstTaskByJobId(user.getTempTableId());
        if (firstTaskByJobId.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }

        var task = firstTaskByJobId.getData();
        var orderId = user.getExtraTableId();
        var orderTask = orderTaskService.createOrderTask(orderId, task.getId());
        if (orderTask.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }

        var simpleResultData = userService.findAllAvailableUsers(task.getId());
        if (simpleResultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessageForAdmin(
                List.of(ADMIN_1_CHAT_ID),
                messageUtils.createNewOrder(orderId),
                botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
            );
            return;
        }

        var availableUsers = simpleResultData.getData();
        var orderTaskData = orderTask.getData();
        orderRepository
            .findById(orderId)
            .ifPresent(existsOrder ->
                availableUsers.forEach(existsUser ->
                    messageSenderService.sendMessage(
                        Long.valueOf(existsUser.getChatId()),
                        messageUtils.getTaskInfo(orderTaskData.getId(), orderTaskData.getOrder(), orderTaskData.getTask()),
                        BotUtils.getOrderKeyboard()
                    )
                )
            );

        messageSenderService.sendMessageForAdmin(
            List.of(ADMIN_1_CHAT_ID),
            messageUtils.createNewOrder(orderId),
            botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
        );
        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
    }
}

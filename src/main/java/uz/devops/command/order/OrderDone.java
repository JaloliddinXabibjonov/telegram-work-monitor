package uz.devops.command.order;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.domain.enumeration.Command.GET_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.EMPLOYEE_MAN_ICON;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.*;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.security.AuthoritiesConstants;
import uz.devops.service.*;
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
    private final TaskService taskService;
    private final UserService userService;
    private final JobService jobService;
    private final TaskRepository taskRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long orderId = botUtils.getOrderIdFromText(message.getText());
        Long taskInfoId = botUtils.getTaskInfoIdFromText(message.getText());

        orderService
            .findOrderById(orderId)
            .ifPresent(order -> {
                order.setStatus(Status.DONE);
                order.setEndDate(Instant.now());
                orderRepository.save(order);

                Task task = taskService.findTaskByInfoId(taskInfoId);
                task.setStatus(Status.DONE);
                taskRepository.save(task);

                messageSenderService.sendEditMessage(
                    message.getChatId(),
                    messageUtils.getAboutMyTask(order, taskService.getTaskInfo(orderId)),
                    message.getMessageId(),
                    null
                );
                messageSenderService.sendMessageForAdmin(List.of(ADMIN_1_CHAT_ID), messageUtils.taskDoneForAdmin(order), null);
            });
        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setBusy(false);
                userRepository.save(user);
            });

        // -- AUTO CREATE ORDER

        Job job = jobService.findJobByOrderId(orderId);
        SimpleResultData<Task> response = jobService.getAvailableTask(job.getId());
        if (Objects.equals(Boolean.FALSE, response.getSuccess())) {
            messageSenderService.sendMessage(message.getChatId(), job.getName() + " bo'yicha vazifalar tugatildi  ⚡", null);
            return;
        }
        Task availableTask = response.getData();
        List<User> availableUsers = userService.checkAvailableUsers(availableTask.getId());

        Order order = new Order();
        order.setStatus(Status.TO_DO);
        order.setCreatedDate(Instant.now());
        order.setCreatedBy(AuthoritiesConstants.ADMIN);
        orderRepository.save(order);

        TaskInfo taskInfo = taskService.findTaskInfoByTaskId(availableTask.getId());

        jobService.addOrderToJob(job.getId(), order);
        orderService.addOrderToTaskInfo(order.getId(), taskInfo.getId());

        if (availableUsers.isEmpty()) {
            log.info("Available users not found");
            messageSenderService.sendMessageForAdmin(
                List.of(ADMIN_1_CHAT_ID),
                "Yangi buyurtma  #" + availableTask.getId() + " yaratildi   ⚡",
                botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
            );
            return;
        }
        var inlineKeyboardMarkup = newInlineKeyboardMarkup(
            List.of(newInlineKeyboardButton(GET_ORDER.getCommandName() + EMPLOYEE_MAN_ICON, GET_ORDER.getCommandName()))
        );

        availableUsers.forEach(user ->
            messageSenderService.sendMessage(
                Long.valueOf(user.getChatId()),
                "Vazifa  #" + availableTask.getId() + "\n" + messageUtils.getTaskInfo(order, taskService.getTaskInfo(order.getId())),
                inlineKeyboardMarkup
            )
        );

        messageSenderService.sendMessageForAdmin(
            List.of(ADMIN_1_CHAT_ID),
            "Yangi buyurtma  #" + orderId + " yaratildi   ⚡",
            botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
        );
    }
}

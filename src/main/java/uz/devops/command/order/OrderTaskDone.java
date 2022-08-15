package uz.devops.command.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Order;
import uz.devops.domain.OrderTask;
import uz.devops.domain.Profession;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.RoleName;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.OrderTaskService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static uz.devops.utils.MessageUtils.SERVER_ERROR;
@Slf4j
@Service(Constants.TASK_DONE)
@RequiredArgsConstructor
public class OrderTaskDone implements Processor {

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private OrderTaskService orderTaskService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MessageUtils messageUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderTaskRepository orderTaskRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = update.getCallbackQuery().getData();
        Long orderTaskId = Long.valueOf(data.split("#")[1]);
        var completedOrderTask = orderTaskService.completedOrderTask(orderTaskId);
        var orderTask = completedOrderTask.getData();
        if (completedOrderTask.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), SERVER_ERROR, null);
            return;
        }
        Long orderId = completedOrderTask.getData().getOrder().getId();
        Optional<OrderTask> optionalOrderTask = orderTaskRepository.findTopByOrderIdAndStatusAndTaskId(orderId, Status.NEW.toString(), orderTask.getTask().getId());
        List<String> adminChatIdList = userRepository.findAllByAuthority(RoleName.ADMIN.toString());
        User user = userRepository.getByChatId(message.getChatId().toString());
        user.setBusy(false);
        userRepository.save(user);
        if (optionalOrderTask.isPresent()) {
            OrderTask orderTask1 = optionalOrderTask.get();
            orderTask1.setStatus(Status.ACTIVE);
            OrderTask save = orderTaskRepository.save(orderTask1);
            log.info("OrderTask is: {}",save);
            for (Profession profession : save.getTask().getProfessions()) {
                for (String s : userRepository.findAllByBusyAndProfessionName(false, profession.getName())) {
                    messageSenderService.sendMessage(Long.parseLong(s), messageUtils.getTaskInfo(
                            save.getId(),
                            save.getOrder(),
                            save.getTask()),
                        BotUtils.getOrderKeyboard(save.getId()));
                }
            }
        }
        boolean exists = orderTaskRepository.existsByOrderIdAndStatusNotLike(orderTask.getOrder().getId(), Status.DONE);
        if (!exists) {
            Order order = orderTask.getOrder();
            order.setStatus(Status.DONE);
            order.setEndDate(Instant.now());
            orderRepository.save(order);
            adminChatIdList.forEach(chatId -> messageSenderService.sendMessage(Long.parseLong(chatId), messageUtils.orderDoneForAdmin(orderTask.getOrder()), null));
            messageSenderService.sendMessage(Long.valueOf(order.getCustomer().getChatId()), MessageUtils.YOUR_ORDER_IS_DONE+"\n"+messageUtils.getOrderInfo(order), null);
        }

        String taskInfoAfterDone = messageUtils.getTaskInfoAfterDone(orderTask.getTask(), orderTask);
        messageSenderService.sendMessage(message.getChatId(), taskInfoAfterDone, null);
        messageSenderService.deleteMessage(message.getMessageId(), message.getChatId().toString());
//        adminChatIdList.forEach(chatId -> messageSenderService.sendMessage(Long.parseLong(chatId), messageUtils.taskDoneForAdmin(orderTaskId, orderTask.getEmployeeUsername()), null));
    }
}

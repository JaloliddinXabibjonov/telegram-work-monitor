package uz.devops.command.order;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Order;
import uz.devops.domain.OrderTask;
import uz.devops.domain.Profession;
import uz.devops.domain.Task;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uz.devops.utils.MessageUtils.ORDER_SUCCESSFULLY_ADDED;

@Service(Constants.CONFIRM_ORDER)
@RequiredArgsConstructor
public class ConfirmOrder implements Processor {

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderTaskRepository orderTaskRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String data = update.getCallbackQuery().getData();
        Long orderId = Long.parseLong(data.split("#")[1]);
        Optional<Order> optionalOrder = orderRepository.findByIdAndStatus(orderId, Status.NEW);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(Status.ACTIVE);
            Order saveOrder = orderRepository.save(order);
            List<Task> taskList = taskRepository.findAllByJobIdOrderById(saveOrder.getJob().getId());
            List<OrderTask> orderTaskList=new ArrayList<>();
            for (int i = 0; i < saveOrder.getCount(); i++) {
                for (int j = 0; j < taskList.size(); j++) {
                    OrderTask orderTask = new OrderTask();
                    orderTask.setOrder(saveOrder);
                    orderTask.setStatus(j == 0?Status.ACTIVE:Status.NEW);
                    orderTask.setTask(taskList.get(j));
                    OrderTask save = orderTaskRepository.save(orderTask);
                    if (j==0){
                        orderTaskList.add(save);
                    }
                }
            }
            for (OrderTask orderTask : orderTaskList) {
                for (Profession profession : orderTask.getTask().getProfessions()) {
                    for (String s : userRepository.findAllByBusyAndProfessionName(false, profession.getName())) {
                        messageSenderService.sendMessage(Long.parseLong(s), messageUtils.getTaskInfo(
                                orderTask.getId(),
                                orderTask.getOrder(),
                                orderTask.getTask()),
                                BotUtils.getOrderKeyboard(orderTask.getId()));
                    }
                }
            }
            messageSenderService.deleteMessage(message.getMessageId(), chatId.toString());
            messageSenderService.sendMessage(
                chatId,
                ORDER_SUCCESSFULLY_ADDED,
                botUtils.createMainButtonsByRole(chatId));
        }
    }
}

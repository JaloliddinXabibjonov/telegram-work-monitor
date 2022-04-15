package uz.devops.command.task;

import static uz.devops.utils.MessageUtils.USER_NOT_FOUND;
import static uz.devops.utils.MessageUtils.USER_WORK_NOT_FOUND;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.Task;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.TaskService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class MyTasks implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderTaskRepository orderTaskRepository;
    private final MessageUtils messageUtils;
    private final UserService userService;
    private final TaskService taskService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();

        var resultData = userService.findUserByChatId(chatId);
        if (resultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(chatId, USER_NOT_FOUND, null);
            return;
        }

        var orderTasks = orderTaskRepository.findAllByEmployeeUsernameAndStatusNotLike(resultData.getData().getTgUsername(), Status.TO_DO);
        if (orderTasks.isEmpty()) {
            messageSenderService.sendMessage(chatId, USER_WORK_NOT_FOUND, null);
            return;
        }

        orderTasks
            .stream()
            .filter(order -> order.getStatus().equals(Status.DONE))
            .collect(Collectors.toList())
            .forEach(orderTask -> {
                SimpleResultData<Task> taskById = taskService.findTaskById(orderTask.getTask().getId());
                messageSenderService.sendMessage(chatId, messageUtils.getAboutMyTask(orderTask, taskById.getData()), null);
            });

        orderTasks
            .stream()
            .filter(orderTask -> orderTask.getStatus().equals(Status.DOING))
            .forEach(orderTask -> {
                var taskById = taskService.findTaskById(orderTask.getTask().getId());
                messageSenderService.sendMessage(
                    chatId,
                    messageUtils.myTask(taskById.getData(), orderTask),
                    botUtils.getTaskOrderKeyboard()
                );
            });
    }
}

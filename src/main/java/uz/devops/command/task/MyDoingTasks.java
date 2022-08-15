package uz.devops.command.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.command.SimpleResultData;
import uz.devops.config.Constants;
import uz.devops.domain.Task;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.TaskService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.util.stream.Collectors;

import static uz.devops.utils.MessageUtils.USER_NOT_FOUND;
import static uz.devops.utils.MessageUtils.USER_WORK_NOT_FOUND;

@RequiredArgsConstructor
@Service(Constants.MY_DOING_TASKS)
public class MyDoingTasks implements Processor {

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

        var orderTasks = orderTaskRepository.findAllByEmployeeUsernameAndStatus(resultData.getData().getTgUsername(), Status.DOING);
        if (orderTasks.isEmpty()) {
            messageSenderService.sendMessage(chatId, USER_WORK_NOT_FOUND, null);
            return;
        }

        orderTasks
            .forEach(orderTask -> {
                var taskById = taskService.findTaskById(orderTask.getTask().getId());
                messageSenderService.sendMessage(
                    chatId,
                    messageUtils.myTask(taskById.getData(), orderTask),
                    botUtils.getTaskOrderKeyboardByOrderTaskId(orderTask.getId())
                );
            });
    }
}

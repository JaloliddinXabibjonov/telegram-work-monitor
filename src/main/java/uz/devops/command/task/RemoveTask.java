package uz.devops.command.task;

import static uz.devops.utils.MessageUtils.TASK_REMOVED;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.TaskRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class RemoveTask implements Processor {

    private final MessageSenderService messageSenderService;
    private final TaskRepository taskRepository;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long parsedTaskId = botUtils.getAnyIdFromText(message.getText());

        if (taskRepository.findById(parsedTaskId).isEmpty()) {
            log.debug("Task not found with id: {}", parsedTaskId);
            return;
        }

        taskRepository.deleteById(parsedTaskId);
        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
        messageSenderService.sendMessage(message.getChatId(), TASK_REMOVED, null);
    }
}

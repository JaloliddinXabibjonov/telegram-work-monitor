package uz.devops.command.task.pipeline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.TaskRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service
public class Pipeline implements Processor {

    private final MessageSenderService messageSenderService;
    private final TaskRepository taskRepository;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();
        //        if (taskRepository.findAll().isEmpty()) {
        //            messageSenderService.sendMessage(message.getChatId(), DOING_WORKS_NOT_FOUND, null);
        //            return;
        //        }
        //        taskRepository.findAll().forEach(task -> messageSenderService.sendMessage(message.getChatId(), messageUtils.getTask(task), null));
    }
}

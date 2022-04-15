package uz.devops.command.task.edit;

import static uz.devops.utils.MessageUtils.TASK_NAME_MODIFIED;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class EditTaskName implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final BotUtils botUtils;
    private final TaskRepository taskRepository;
    private final UserService userService;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (user.getState() == BotState.ENTER_TASK_NEW_NAME) {
                    taskRepository
                        .findById(user.getTempTableId())
                        .ifPresent(task -> {
                            task.setName(update.getMessage().getText());
                            taskRepository.save(task);

                            user.setState(null);
                            userRepository.save(user);

                            messageSenderService.sendMessage(
                                update.getMessage().getChatId(),
                                TASK_NAME_MODIFIED,
                                botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
                            );
                        });
                }
            });
    }
}

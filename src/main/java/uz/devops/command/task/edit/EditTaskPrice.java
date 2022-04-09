package uz.devops.command.task.edit;

import static uz.devops.utils.MessageUtils.TASK_PRICE_MODIFIED;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class EditTaskPrice implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final BotUtils botUtils;
    private final TaskRepository taskRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        Optional<User> optionalUser = userRepository.findByChatId(String.valueOf(message.getChatId()));

        if (optionalUser.isEmpty()) {
            log.debug("User not found with chatId: {}", message.getChatId());
            return;
        }

        User user = optionalUser.get();

        if (user.getState() == BotState.ENTER_TASK_NEW_PRICE) {
            taskRepository
                .findById(user.getTempTableId())
                .ifPresent(task -> {
                    //                        task.setPrice(update.getMessage().getText());
                    taskRepository.save(task);

                    user.setState(null);
                    userRepository.save(user);

                    messageSenderService.sendMessage(
                        update.getMessage().getChatId(),
                        TASK_PRICE_MODIFIED,
                        botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
                    );
                });
        }
    }
}

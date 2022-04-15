package uz.devops.command.task;

import static uz.devops.utils.MessageUtils.ENTER_TASK_DESCRIPTION;
import static uz.devops.utils.MessageUtils.ONLY_ENTER_NUMBER;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Task;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.JobRepository;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class CreateTask implements Processor {

    private final MessageSenderService messageSenderService;
    private final ProfessionRepository professionRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final MessageUtils messageUtils;
    private final UserService userService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (user.getState() == BotState.ENTER_TASK_NAME) {
                    Task task = new Task();
                    task.setName(message.getText());
                    taskRepository.save(task);

                    user.setState(BotState.ENTER_TASK_PRICE);
                    user.setExtraTableId(task.getId());
                    userRepository.save(user);

                    jobRepository
                        .findById(user.getTempTableId())
                        .ifPresent(job -> {
                            task.setJob(job);
                            taskRepository.save(task);
                        });

                    messageSenderService.sendMessage(message.getChatId(), messageUtils.exampleEnterPrice(), null);
                    return;
                }

                if (user.getState().equals(BotState.ENTER_TASK_PRICE)) {
                    if (Objects.equals(botUtils.checkNumberValidation(message.getText()), Boolean.FALSE)) {
                        messageSenderService.sendMessage(message.getChatId(), ONLY_ENTER_NUMBER, null);
                        return;
                    }

                    taskRepository
                        .findById(user.getExtraTableId())
                        .ifPresent(task -> {
                            task.setPrice(Long.valueOf(message.getText()));
                            taskRepository.save(task);

                            user.setState(BotState.ENTER_TASK_DESCRIPTION);
                            userRepository.save(user);
                            messageSenderService.sendMessage(message.getChatId(), ENTER_TASK_DESCRIPTION, null);
                        });
                    return;
                }

                if (user.getState().equals(BotState.ENTER_TASK_DESCRIPTION)) {
                    user.setState(null);
                    userRepository.save(user);
                    taskRepository
                        .findById(user.getExtraTableId())
                        .ifPresent(task -> {
                            task.setDescription(message.getText());
                            taskRepository.save(task);
                        });
                    var professionsKeyboard = BotUtils.getProfessionsKeyboard(professionRepository);
                    messageSenderService.sendMessage(
                        message.getChatId(),
                        messageUtils.setRoleToTask(user.getExtraTableId()),
                        professionsKeyboard
                    );
                }
            });
    }
}

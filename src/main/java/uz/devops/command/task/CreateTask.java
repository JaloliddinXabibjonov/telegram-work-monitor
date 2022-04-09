package uz.devops.command.task;

import static uz.devops.domain.enumeration.Command.ASSIGN_ROLE_TO_TASK;

import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Task;
import uz.devops.domain.enumeration.BotState;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.JobRepository;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class CreateTask implements Processor {

    private final MessageSenderService messageSenderService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JobRepository jobRepository;
    private final ProfessionRepository professionRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (user.getState() == BotState.ENTER_TASK_NAME) {
                    user.setState(null);
                    userRepository.save(user);

                    Task task = new Task();
                    task.setName(message.getText());
                    task.setStatus(Status.NEW);
                    task.setCreatedDate(Instant.now());
                    taskRepository.save(task);

                    jobRepository
                        .findById(user.getTempTableId())
                        .ifPresent(job -> {
                            if (job.getTasks() != null) {
                                job.getTasks().add(task);
                                jobRepository.save(job);
                            } else {
                                job.setTasks(Set.of(task));
                                jobRepository.save(job);
                            }
                        });

                    var professionsKeyboard = BotUtils.getProfessionsKeyboard(professionRepository);
                    messageSenderService.sendMessage(
                        message.getChatId(),
                        ASSIGN_ROLE_TO_TASK.getCommandName() + "\n" + "\n" + "Task  #" + task.getId(),
                        professionsKeyboard
                    );
                }
            });
    }
}

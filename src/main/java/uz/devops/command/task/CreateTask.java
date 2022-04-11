package uz.devops.command.task;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Task;
import uz.devops.domain.enumeration.BotState;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.JobRepository;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.security.AuthoritiesConstants;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;

@RequiredArgsConstructor
@Service
public class CreateTask implements Processor {

    private final MessageSenderService messageSenderService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JobRepository jobRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (user.getState() == BotState.ENTER_TASK_NAME) {
                    Task task = new Task();
                    task.setName(message.getText());
                    task.setStatus(Status.NEW);
                    task.setCreatedBy(AuthoritiesConstants.ADMIN);
                    task.setCreatedDate(Instant.now());
                    taskRepository.save(task);

                    user.setState(BotState.ENTER_TASK_PRICE);
                    user.setExtraTableId(task.getId());
                    userRepository.save(user);

                    jobRepository
                        .findById(user.getTempTableId())
                        .ifPresent(job -> {
                            task.setJob(job);
                            taskRepository.save(task);
                            //                            if (job.getTasks() != null) {
                            //                                job.getTasks().add(task);
                            //                            } else {
                            //                                job.setTasks(Set.of(task));
                            //                            }
                            //                            jobRepository.save(job);
                        });

                    messageSenderService.sendMessage(message.getChatId(), "Narxini kiriting", null);
                }
            });
    }
}

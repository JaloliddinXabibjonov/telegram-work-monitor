package uz.devops.command.taskInfo;

import static uz.devops.domain.enumeration.Command.ASSIGN_ROLE_TO_TASK;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.TaskInfo;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.TaskInfoRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.TaskService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@Service
@RequiredArgsConstructor
public class CreateTaskInfo implements Processor {

    private final MessageSenderService messageSenderService;
    private final ProfessionRepository professionRepository;
    private final TaskInfoRepository taskInfoRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TaskService taskService;
    private static Long TASK_INFO_ID = 0L;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (user.getState().equals(BotState.ENTER_TASK_PRICE)) {
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setPrice(message.getText());
                    taskInfoRepository.save(taskInfo);

                    TASK_INFO_ID = taskInfo.getId();

                    user.setState(BotState.ENTER_TASK_DESCRIPTION);
                    userRepository.save(user);

                    messageSenderService.sendMessage(message.getChatId(), "Vazifa haqida to'liqroq ma'lumot kiriting", null);
                    return;
                }
                if (user.getState().equals(BotState.ENTER_TASK_DESCRIPTION)) {
                    user.setState(null);
                    userRepository.save(user);

                    taskInfoRepository
                        .findById(TASK_INFO_ID)
                        .ifPresent(taskInfo -> {
                            taskInfo.setDescription(message.getText());
                            taskInfoRepository.save(taskInfo);

                            taskService.addTaskToTaskInfo(taskInfo, user.getExtraTableId());

                            var professionsKeyboard = BotUtils.getProfessionsKeyboard(professionRepository);
                            messageSenderService.sendMessage(
                                message.getChatId(),
                                ASSIGN_ROLE_TO_TASK.getCommandName() + "\n" + "\n" + "Task  #" + user.getExtraTableId(),
                                professionsKeyboard
                            );
                        });
                }
            });
    }
}

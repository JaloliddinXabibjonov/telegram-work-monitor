package uz.devops.command.task;

import static uz.devops.domain.enumeration.Command.*;
import static uz.devops.utils.BotUtils.*;
import static uz.devops.utils.Icons.REJECT_MAN_ICON;
import static uz.devops.utils.MessageUtils.CREATE_TASK;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Task;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service(Constants.CONFIRM_TASK_PROFESSION)
public class ConfirmTaskProfession implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProfessionRepository professionRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = update.getCallbackQuery().getData();
        Long taskId = Long.valueOf(data.split("#")[1]);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            if (task.getProfessions().size() > 0) {
                userRepository
                    .findByChatId(String.valueOf(message.getChatId()))
                    .ifPresent(user -> {
                        var inlineKeyboardMarkup = newInlineKeyboardMarkup(
                            List.of(
                                newInlineKeyboardButton(CREATE_TASK, ADD_TASK_TO_JOB.getCommandName()+"#"+ task.getJob().getId()),
                                newInlineKeyboardButton(REJECT_MAN_ICON, GET_MAIN_KEYBOARDS.getCommandName())
                            )
                        );
                        messageSenderService.sendMessage(message.getChatId(), messageUtils.addNewTask(user.getTempTableId()), inlineKeyboardMarkup);
                        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
                    });
            } else {
                messageSenderService.sendMessage(message.getChatId(), messageUtils.setRoleToTask(taskId), getProfessionsKeyboard(professionRepository, SET_PROFESSION_TO_TASK.getCommandName() + "#"));
            }
        }

    }
}

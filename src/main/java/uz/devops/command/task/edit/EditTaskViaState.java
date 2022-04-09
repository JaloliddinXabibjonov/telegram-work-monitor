package uz.devops.command.task.edit;

import static uz.devops.domain.enumeration.Command.EDIT_TASK_NAME;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.EDIT_PEN_ICON;
import static uz.devops.utils.MessageUtils.EDIT_NAME;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;

@RequiredArgsConstructor
@Service
public class EditTaskViaState implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                if (user.getState().equals(BotState.EDIT_TASK)) {
                    user.setState(null);
                    userRepository.save(user);

                    var markupInline = newInlineKeyboardMarkup(
                        List.of(newInlineKeyboardButton(EDIT_NAME + EDIT_PEN_ICON, EDIT_TASK_NAME.getCommandName()))
                    );

                    taskRepository
                        .findById(Long.valueOf(update.getCallbackQuery().getData()))
                        .ifPresent(task ->
                            messageSenderService.sendEditMessage(
                                message.getChatId(),
                                "Task  #" + task.getId() + "\n" + "Nomi: " + task.getName(),
                                message.getMessageId(),
                                markupInline
                            )
                        );
                }
            });
    }
}

package uz.devops.command.task;

import static uz.devops.domain.enumeration.Command.ADD_TASK_TO_JOB;
import static uz.devops.domain.enumeration.Command.GET_MAIN_KEYBOARDS;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.REJECT_MAN_ICON;
import static uz.devops.utils.MessageUtils.CREATE_TASK;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service
public class ConfirmTaskProfession implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                var inlineKeyboardMarkup = newInlineKeyboardMarkup(
                    List.of(
                        newInlineKeyboardButton(CREATE_TASK, ADD_TASK_TO_JOB.getCommandName()),
                        newInlineKeyboardButton(REJECT_MAN_ICON, GET_MAIN_KEYBOARDS.getCommandName())
                    )
                );
                messageSenderService.sendMessage(message.getChatId(), messageUtils.addNewTask(user.getTempTableId()), inlineKeyboardMarkup);
                messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
            });
    }
}

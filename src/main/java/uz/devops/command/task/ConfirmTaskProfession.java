package uz.devops.command.task;

import static uz.devops.domain.enumeration.Command.ADD_TASK_TO_JOB;
import static uz.devops.domain.enumeration.Command.GET_MAIN_KEYBOARDS;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.REJECT_MAN_ICON;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;

@RequiredArgsConstructor
@Service
public class ConfirmTaskProfession implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                var inlineKeyboardMarkup = newInlineKeyboardMarkup(
                    List.of(
                        newInlineKeyboardButton("Vazifa ➕", ADD_TASK_TO_JOB.getCommandName()),
                        newInlineKeyboardButton(REJECT_MAN_ICON, GET_MAIN_KEYBOARDS.getCommandName())
                    )
                );
                messageSenderService.sendMessage(
                    message.getChatId(),
                    "Ish  #" + user.getTempTableId() + "\n" + "Yana vazifa qo'shmoqchimisiz ?",
                    inlineKeyboardMarkup
                );
                messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
            });
    }
}

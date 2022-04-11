package uz.devops.command.job;

import static uz.devops.domain.enumeration.Command.CHOOSE_TASK_TO_EDIT;
import static uz.devops.domain.enumeration.Command.EDIT_JOB;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.service.MessageSenderService;

@RequiredArgsConstructor
@Service
public class ChooseOneToEdit implements Processor {

    private final MessageSenderService messageSenderService;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();

        var markupInline = newInlineKeyboardMarkup(
            List.of(
                newInlineKeyboardButton("Ish  \uD83D\uDD8C", EDIT_JOB.getCommandName()),
                newInlineKeyboardButton("Vazifa  \uD83D\uDD8C", CHOOSE_TASK_TO_EDIT.getCommandName())
            )
        );

        messageSenderService.sendEditMessage(message.getChatId(), message.getText(), message.getMessageId(), markupInline);
    }
}

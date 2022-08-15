package uz.devops.command.job;

import static uz.devops.domain.enumeration.Command.CHOOSE_TASK_TO_EDIT;
import static uz.devops.domain.enumeration.Command.EDIT_JOB;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.MessageUtils.JOB;
import static uz.devops.utils.MessageUtils.TASK;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.service.MessageSenderService;

@Service(Constants.CHOOSE_ONE_TO_EDIT)
@RequiredArgsConstructor
public class ChooseOneToEdit implements Processor {

    private final MessageSenderService messageSenderService;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();

        var markupInline = newInlineKeyboardMarkup(
            List.of(
                newInlineKeyboardButton(JOB, EDIT_JOB.getCommandName()),
                newInlineKeyboardButton(TASK, CHOOSE_TASK_TO_EDIT.getCommandName())
            )
        );

        messageSenderService.sendEditMessage(message.getChatId(), message.getText(), message.getMessageId(), markupInline);
    }
}

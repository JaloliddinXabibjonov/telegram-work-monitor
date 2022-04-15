package uz.devops.command.job;

import static uz.devops.domain.enumeration.Command.EDIT_JOB;
import static uz.devops.domain.enumeration.Command.EDIT_JOB_NAME;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.EDIT_PEN_ICON;
import static uz.devops.utils.MessageUtils.EDIT_NAME;
import static uz.devops.utils.MessageUtils.ENTER_NEW_NAME;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.BotState;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@Service
@RequiredArgsConstructor
public class EditJob implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserService userService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = callbackQuery.getMessage();
        String data = callbackQuery.getData();
        Long parsedJobId = botUtils.getAnyIdFromText(message.getText());

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (data.equals(EDIT_JOB.getCommandName())) {
                    var markupInline = newInlineKeyboardMarkup(
                        List.of(newInlineKeyboardButton(EDIT_NAME + EDIT_PEN_ICON, EDIT_JOB_NAME.getCommandName()))
                    );
                    messageSenderService.sendEditMessage(message.getChatId(), message.getText(), message.getMessageId(), markupInline);
                } else if (data.equals(EDIT_JOB_NAME.getCommandName())) {
                    userService.changeUserFields(user, BotState.ENTER_JOB_NEW_NAME, parsedJobId);
                    messageSenderService.sendMessage(message.getChatId(), ENTER_NEW_NAME, null);
                }
            });
    }
}

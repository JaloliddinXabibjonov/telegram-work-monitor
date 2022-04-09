package uz.devops.command.task.edit;

import static uz.devops.domain.enumeration.Command.*;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.EDIT_PEN_ICON;
import static uz.devops.utils.MessageUtils.*;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class EditTask implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final BotUtils botUtils;
    private final UserService userService;

    @Override
    public void execute(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = callbackQuery.getMessage();
        Long parsedTaskId = botUtils.getAnyIdFromText(message.getText());

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                if (callbackQuery.getData().equals(EDIT_TASK.getCommandName())) {
                    var markupInline = newInlineKeyboardMarkup(
                        List.of(
                            newInlineKeyboardButton(EDIT_NAME + EDIT_PEN_ICON, EDIT_TASK_NAME.getCommandName())
                            //                                newInlineKeyboardButton(EDIT_PRICE + EDIT_PEN_ICON, EDIT_TASK_PRICE.getCommandName())
                        )
                    );
                    messageSenderService.sendEditMessage(message.getChatId(), message.getText(), message.getMessageId(), markupInline);
                } else if (callbackQuery.getData().equals(EDIT_TASK_NAME.getCommandName())) {
                    userService.changeUserFields(user, BotState.ENTER_TASK_NEW_NAME, parsedTaskId);
                    messageSenderService.sendMessage(message.getChatId(), ENTER_NEW_NAME, null);
                } else if (callbackQuery.getData().equals(EDIT_TASK_PRICE.getCommandName())) {
                    userService.changeUserFields(user, BotState.ENTER_TASK_NEW_PRICE, parsedTaskId);
                    messageSenderService.sendMessage(message.getChatId(), ENTER_NEW_PRICE, null);
                }
            });
    }
}

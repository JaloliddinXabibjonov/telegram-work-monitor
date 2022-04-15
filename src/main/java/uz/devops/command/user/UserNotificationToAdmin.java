package uz.devops.command.user;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.domain.enumeration.Command.REMOVE_USER;
import static uz.devops.domain.enumeration.Command.SET_ROLE_TO_USER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.Icons.CONFIRM_ICON;
import static uz.devops.utils.Icons.REMOVE_ICON;
import static uz.devops.utils.MessageUtils.MESSAGE_TO_USER_ON_REG;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class UserNotificationToAdmin implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserService userService;
    private final BotUtils botUtils;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        var user = userService.createNewUser(update.getMessage());
        messageSenderService.sendMessage(update.getMessage().getChatId(), MESSAGE_TO_USER_ON_REG, botUtils.keyboardRemove(true));

        var inlineKeyboardMarkup = newInlineKeyboardMarkup(
            List.of(
                newInlineKeyboardButton(CONFIRM_ICON, SET_ROLE_TO_USER.getCommandName()),
                newInlineKeyboardButton(REMOVE_ICON, REMOVE_USER.getCommandName())
            )
        );

        messageSenderService.sendMessageForAdmin(
            List.of(ADMIN_1_CHAT_ID),
            messageUtils.getRegisteredNewUserInfo(user),
            inlineKeyboardMarkup
        );
    }
}

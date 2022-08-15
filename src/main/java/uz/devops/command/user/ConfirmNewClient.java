package uz.devops.command.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.command.Processor;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

import java.time.Instant;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.utils.MessageUtils.CONFIRMATION_MESSAGE;
import static uz.devops.utils.MessageUtils.USER_CONFIRMED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmNewClient implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = update.getCallbackQuery().getData();
        Long parsedUserId = botUtils.getIdForSetRole(data);
        log.debug("Parsed user chat id, {}", parsedUserId);
        var user = userRepository.getByChatId(parsedUserId.toString());
//        if (userById.getSuccess().equals(Boolean.FALSE)) {
//            messageSenderService.sendMessage(message.getChatId(), USER_NOT_FOUND, null);
//            return;
//        }

        user.setConfirmed(true);
        user.setConfirmedDate(Instant.now());
        userRepository.save(user);

        messageSenderService.sendMessage(
            Long.valueOf(user.getChatId()),
            CONFIRMATION_MESSAGE,
            botUtils.createMainButtonForClient()
        );
        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
        messageSenderService.sendMessage(
            Long.valueOf(ADMIN_1_CHAT_ID),
            USER_CONFIRMED,
            new ReplyKeyboardRemove(true));
    }
}

package uz.devops.command.user;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.utils.MessageUtils.CONFIRMATION_MESSAGE;
import static uz.devops.utils.MessageUtils.USER_CONFIRMED;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.User;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConfirmUserProfession implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long parsedUserId = botUtils.getIdForSetRole(message.getText());
        Optional<User> optionalUser = userRepository.findById(parsedUserId);

        if (optionalUser.isEmpty()) {
            log.debug("User not found with id: {}", parsedUserId);
            return;
        }

        User user = optionalUser.get();
        user.setConfirmed(true);
        user.setConfirmedDate(Instant.now());
        userRepository.save(user);
        messageSenderService.sendMessage(
            Long.valueOf(user.getChatId()),
            CONFIRMATION_MESSAGE,
            botUtils.getMainReplyKeyboard(user.getChatId())
        );
        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
        messageSenderService.sendMessage(
            Long.valueOf(ADMIN_1_CHAT_ID),
            USER_CONFIRMED,
            botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
        );
    }
}

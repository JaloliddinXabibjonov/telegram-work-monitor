package uz.devops.command.user;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.utils.MessageUtils.REJECTED_USER_MESSAGE;
import static uz.devops.utils.MessageUtils.USER_REMOVED;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class RemoveUser implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long parsedUserId = botUtils.getUserIdFromText(message.getText());

        userRepository
            .findById(parsedUserId)
            .ifPresent(user -> {
                userRepository.deleteById(user.getId());
                messageSenderService.sendMessage(Long.valueOf(user.getChatId()), REJECTED_USER_MESSAGE, null);
            });

        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
        messageSenderService.sendMessage(
            Long.valueOf(ADMIN_1_CHAT_ID),
            USER_REMOVED,
            botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
        );
    }
}

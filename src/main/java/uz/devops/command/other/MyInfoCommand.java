package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service
public class MyInfoCommand implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserService userService;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        userService.findByChatId(chatId).ifPresent(user -> messageSenderService.sendMessage(chatId, messageUtils.getUserInfo(user), null));
    }
}

package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class BackCommand implements Processor {

    private final MessageSenderService messageSenderService;
    private final BotUtils botUtils;
    private final UserRepository userRepository;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        userRepository
            .findByChatId(String.valueOf(chatId))
            .ifPresent(user -> {
                user.setState(null);
                userRepository.save(user);
            });

        messageSenderService.sendMessage(chatId, "Back  \uD83D\uDD19", botUtils.getMainReplyKeyboard(String.valueOf(chatId)));
    }
}

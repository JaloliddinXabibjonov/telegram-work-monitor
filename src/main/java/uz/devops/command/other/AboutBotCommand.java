package uz.devops.command.other;

import static uz.devops.utils.MessageUtils.HELP_MESSAGE;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.service.MessageSenderService;

@RequiredArgsConstructor
@Service
public class AboutBotCommand implements Processor {

    private final MessageSenderService messageSenderService;

    @Override
    public void execute(Update update) {
        messageSenderService.sendMessage(update.getMessage().getChatId(), HELP_MESSAGE, null);
    }
}

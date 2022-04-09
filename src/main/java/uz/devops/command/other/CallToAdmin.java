package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.service.MessageSenderService;

@RequiredArgsConstructor
@Service
public class CallToAdmin implements Processor {

    private final MessageSenderService messageSenderService;

    @Override
    public void execute(Update update) {
        messageSenderService.sendMessage(update.getMessage().getChatId(), "https://t.me/Asliddin_Mukhiddinov", null);
    }
}

package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class GetMainKeyboards implements Processor {

    private final MessageSenderService messageSenderService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        messageSenderService.sendMessage(message.getChatId(), "Ok", botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId())));
        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
    }
}

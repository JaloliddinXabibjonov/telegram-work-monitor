package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.service.MessageSenderService;

@RequiredArgsConstructor
@Service(Constants.DELETE_MESSAGE)
public class DeleteMessageCommand implements Processor {

    private final MessageSenderService messageSenderService;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        messageSenderService.deleteMessage(messageId,chatId.toString());
    }
}

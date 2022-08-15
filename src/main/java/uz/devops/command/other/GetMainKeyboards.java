package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service(Constants.GET_MAIN_KEYBOARDS)
public class GetMainKeyboards implements Processor {

    @Autowired
    private  MessageSenderService messageSenderService;
    @Autowired
    private BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = botUtils.createMainButtonsByRole(message.getChatId());
        messageSenderService.sendMessage(message.getChatId(), MessageUtils.JOB_SUCCESSFULLY_ADDED,replyKeyboardMarkup );
        messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
    }
}

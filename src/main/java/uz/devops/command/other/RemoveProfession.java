package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.service.MessageSenderService;
import uz.devops.service.ProfessionService;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service(Constants.REMOVE_PROFESSION_BY_NAME)
public class RemoveProfession implements Processor {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private ProfessionService professionService;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        String profName = update.getCallbackQuery().getData().split("#")[1];
        String text;
        try {
            professionService.delete(profName);
            text = MessageUtils.PROFESSION_DELETED;
        } catch (Exception e) {
            text = "Tanlangan kasb o'chirilmadi!";
        }
        messageSenderService.deleteMessage(messageId, chatId.toString());
        messageSenderService.sendMessage(message.getChatId(), text, null);
    }
}

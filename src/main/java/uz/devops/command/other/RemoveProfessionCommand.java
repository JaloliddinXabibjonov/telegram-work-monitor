package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.repository.ProfessionRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service(Constants.REMOVE_PROFESSION_COMMAND)
public class RemoveProfessionCommand implements Processor {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private ProfessionRepository professionRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        messageSenderService.deleteMessage(messageId, chatId.toString());
        messageSenderService.sendMessage(message.getChatId(), MessageUtils.SELECT_PROFESSION_FOR_REMOVING, BotUtils.getProfessionsKeyboard(professionRepository, Constants.REMOVE_PROFESSION_BY_NAME +"#"));
    }
}

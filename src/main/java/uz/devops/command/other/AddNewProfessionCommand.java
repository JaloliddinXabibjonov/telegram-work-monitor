package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.MessageUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Service(Constants.ADD_NEW_PROFESSION)
public class AddNewProfessionCommand implements Processor {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        Optional<User> optionalUser = userRepository.findByChatId(chatId.toString());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setState(BotState.ENTER_NEW_PROFESSION_NAME);
            userRepository.save(user);
            messageSenderService.sendMessage(message.getChatId(), MessageUtils.ENTER_NEW_PROFESSION_NAME,new ReplyKeyboardRemove(true));
            messageSenderService.deleteMessage(messageId, chatId.toString());
        }else {
            messageSenderService.sendMessage(chatId, MessageUtils.USER_NOT_FOUND, null);
        }
    }
}

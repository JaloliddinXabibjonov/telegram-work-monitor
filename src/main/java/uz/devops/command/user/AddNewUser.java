package uz.devops.command.user;

import static uz.devops.domain.enumeration.Command.SET_PROFESSION_TO_USER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.MessageUtils.CHOOSE_ROLE;
import static uz.devops.utils.MessageUtils.ROLE_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Authority;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.AuthorityRepository;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service(Constants.ADD_NEW_USER)
@RequiredArgsConstructor
public class AddNewUser implements Processor {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Optional<User> optionalUser = userRepository.findByChatId(chatId.toString());
        if (optionalUser.isEmpty()){
            messageSenderService.sendMessage(chatId, "Ro'yxatdan o'tmagansiz!", null);
            return;
        }
        User user = optionalUser.get();
        user.setState(BotState.ENTER_NEW_USER_PHONE_NUMBER);
        userRepository.save(user);
        messageSenderService.sendMessage(chatId, "Foydalanuvchi telefon raqamini kiriting: ", new ReplyKeyboardRemove(true));
    }
}

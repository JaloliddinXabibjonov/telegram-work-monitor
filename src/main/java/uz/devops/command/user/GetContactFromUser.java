package uz.devops.command.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.domain.Authority;
import uz.devops.domain.User;
import uz.devops.repository.AuthorityRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.MessageUtils.CHOOSE_ROLE;
import static uz.devops.utils.MessageUtils.REGISTERED_NEW_USER;
@Slf4j
@Service
@RequiredArgsConstructor
public class GetContactFromUser implements Processor {

    private final MessageSenderService messageSenderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        String phoneNumber = update.getMessage().getContact().getPhoneNumber().startsWith("+")?update.getMessage().getContact().getPhoneNumber():("+"+update.getMessage().getContact().getPhoneNumber());
        log.info(phoneNumber);
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()){
            messageSenderService.sendMessage(chatId, "Siz ro'yxatdan o'tmagansiz!", null);
            return;
        }
        User user = optionalUser.get();
        String userName = update.getMessage().getChat().getUserName();
        user.setChatId(chatId.toString());
        user.setTgUsername(userName);
        user.setConfirmed(true);
        user.setActivated(true);
        user.setConfirmedDate(Instant.now());
        user = userRepository.save(user);
        ReplyKeyboardMarkup replyKeyboardMarkup = botUtils.createMainButtonsByRole(Long.parseLong(user.getChatId()));
        messageSenderService.sendMessage(chatId, MessageUtils.CHOOSE_ONE_OF_THESE, replyKeyboardMarkup);
    }

}

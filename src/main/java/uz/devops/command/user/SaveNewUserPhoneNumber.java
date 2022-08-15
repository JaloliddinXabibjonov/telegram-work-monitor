package uz.devops.command.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.domain.Authority;
import uz.devops.domain.User;
import uz.devops.repository.AuthorityRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

import java.util.ArrayList;
import java.util.List;

import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.MessageUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveNewUserPhoneNumber implements Processor {

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BotUtils botUtils;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();
        String phoneNumber = message.getText();
        boolean exists = userRepository.existsByPhoneNumber(phoneNumber);
        if (exists){
            messageSenderService.sendMessage(message.getChatId(), "Ushbu raqamli foydalanuvchi allaqachon ro'yxatdan o'tkazilgan!", botUtils.createMainButtonsByRole(message.getChatId()));

            return;
        }
        User user = new User();
        user.setPhoneNumber(message.getText());
        user=userRepository.save(user);
        User admin = userRepository.getByChatId(message.getChatId().toString());
        admin.setState(null);
        userRepository.save(admin);

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<Authority> authorityList = authorityRepository.findAll();
        for (Authority authority : authorityList) {
            if (authority.getName().equals("ADMIN")){
                continue;
            }
            row.add(newInlineKeyboardButton(authority.getName(), "CHOOSE_ROLE#" + authority.getName()+"#"+user.getPhoneNumber()));
        }
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        messageSenderService.sendMessage(
            message.getChatId(),
            CHOOSE_ROLE,
            inlineKeyboardMarkup
        );

    }
}

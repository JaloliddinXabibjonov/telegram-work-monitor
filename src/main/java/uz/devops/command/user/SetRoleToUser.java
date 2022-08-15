package uz.devops.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Authority;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.BotState;
import uz.devops.domain.enumeration.RoleName;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

import java.util.Collections;
import java.util.Optional;

import static uz.devops.domain.enumeration.Command.SET_PROFESSION_TO_USER;
import static uz.devops.utils.MessageUtils.CHOOSE_PROFESSION;
import static uz.devops.utils.MessageUtils.NEW_USER_ADDED;

@Slf4j
@Service(Constants.SET_ROLE_TO_USER)
public class SetRoleToUser implements Processor {
    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private BotUtils botUtils;


    @Override
    public void execute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String fullData = update.getCallbackQuery().getData();
        String role = fullData.split("#")[1];
        String phoneNumber = fullData.split("#")[2];
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        InlineKeyboardMarkup inlineKeyboardMarkup;
        String message = "";
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setAuthorities(Collections.singleton(new Authority(role)));
            userRepository.save(user);
            if (role.equals(RoleName.ISHCHI.toString())) {
                inlineKeyboardMarkup = BotUtils.getProfessionsKeyboard(professionRepository, SET_PROFESSION_TO_USER.getCommandName() + "#"+phoneNumber+"#");
                message = CHOOSE_PROFESSION;
                messageSenderService.sendMessage(chatId, message, inlineKeyboardMarkup);
            } else {
                message = NEW_USER_ADDED;
                messageSenderService.sendMessage(chatId, message, botUtils.createMainButtonsByRole(chatId));
            }
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            messageSenderService.deleteMessage(messageId, chatId.toString());
        }
    }

}

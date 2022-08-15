package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Profession;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Service(Constants.SAVE_NEW_PROFESSION)
public class SaveNewProfession implements Processor {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BotUtils botUtils;
    @Autowired
    private ProfessionRepository professionRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String profName = message.getText();
        Optional<Profession> optionalProfession = professionRepository.findByName(profName);
        if (optionalProfession.isPresent()){
            messageSenderService.sendMessage(chatId, MessageUtils.PROFESSION_NAME_IS_ALREADY_EXIST, null);
            return;
        }
        Profession profession=new Profession();
        profession.setName(profName);
        professionRepository.save(profession);
        Optional<User> optionalUser = userRepository.findByChatId(chatId.toString());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setState(null);
            userRepository.save(user);
            messageSenderService.sendMessage(update.getMessage().getChatId(), MessageUtils.NEW_PROFESSION_SAVED_SUCCESSFULLY, botUtils.createMainButtonsByRole(chatId) );
        }else {
            messageSenderService.sendMessage(chatId, MessageUtils.USER_NOT_FOUND, null);
        }
    }
}

package uz.devops.command.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.WorkMonitorBot;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.User;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.time.Instant;
import java.util.Optional;

import static uz.devops.domain.enumeration.Command.*;
import static uz.devops.utils.MessageUtils.*;

@Service(Constants.CONFIRM_PROFESSION)
@RequiredArgsConstructor
public class ConfirmProfession implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    @Autowired
    private BotUtils botUtils;
    @Autowired
    private ProfessionRepository professionRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String data = update.getCallbackQuery().getData();
        String phoneNumber = data.split("#")[1];
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getProfessions().size() > 0) {
                user.setConfirmed(true);
                user.setConfirmedDate(Instant.now());
                userRepository.save(user);
                messageSenderService.sendMessage(
                    chatId,
                    NEW_USER_ADDED,
                    botUtils.createMainButtonsByRole(chatId)
                );
                messageSenderService.deleteMessage(message.getMessageId(), chatId.toString());
            } else {
                messageSenderService.deleteMessage(message.getMessageId(), chatId.toString());
                messageSenderService.sendMessage(
                    chatId,
                    CHOOSE_PROFESSION,
                    BotUtils.getProfessionsKeyboard(professionRepository, SET_PROFESSION_TO_USER.getCommandName() + "#"+phoneNumber+"#")
                );
            }
        }

    }
}

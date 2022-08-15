package uz.devops.command.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Profession;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uz.devops.domain.enumeration.Command.CONFIRM_PROFESSION;
import static uz.devops.domain.enumeration.Command.SET_PROFESSION_TO_USER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButtonRow;
import static uz.devops.utils.MessageUtils.CONFIRM;

@Service(Constants.SET_PROFESSION_TO_USER)
@RequiredArgsConstructor
public class SetProfessionToUser implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProfessionRepository professionRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = update.getCallbackQuery().getData();
        String[] split = data.split("#");
        String phoneNumber = split[1];
        String professionName = split[2];
        userService.checkUserProfessionByPhoneNumber(professionName, phoneNumber);
        Map<Profession, Boolean> professionMap = new HashMap<>();

        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        professionRepository.findAll().forEach(profession -> professionMap.put(profession, professionMap.get(profession) != null));

        userRepository
            .findByPhoneNumber(phoneNumber)
            .ifPresent(user ->
                user.getProfessions().forEach(profession -> professionMap.put(profession, professionMap.get(profession) != null))
            );

        professionMap.forEach((key, value) -> rows.add(BotUtils.newInlineCheckedKeyboardButtonRow(key.getName(), SET_PROFESSION_TO_USER.getCommandName() + "#" +phoneNumber+"#"+ key.getName(), value)));
//        rows.add(row);
        rows.add(newInlineKeyboardButtonRow(CONFIRM, CONFIRM_PROFESSION.getCommandName()+"#"+phoneNumber));
        markupInline.setKeyboard(rows);

        messageSenderService.sendEditMessage(message.getChatId(), message.getText(), message.getMessageId(), markupInline);
    }
}

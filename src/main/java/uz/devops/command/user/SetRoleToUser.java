package uz.devops.command.user;

import static uz.devops.domain.enumeration.Command.CONFIRM_USER_PROFESSION;
import static uz.devops.utils.BotUtils.newInlineCheckedKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardButtonRow;
import static uz.devops.utils.MessageUtils.CONFIRM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.domain.Profession;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class SetRoleToUser implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProfessionRepository professionRepository;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long parsedUserId = botUtils.getIdForSetRole(message.getText());

        userService.checkUserProfession(update.getCallbackQuery().getData(), parsedUserId);

        var markupInline = new InlineKeyboardMarkup();
        var professionMap = new HashMap<Profession, Boolean>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        professionRepository.findAll().forEach(profession -> professionMap.put(profession, professionMap.get(profession) != null));

        userRepository
            .findById(parsedUserId)
            .ifPresent(user ->
                user.getProfessions().forEach(profession -> professionMap.put(profession, professionMap.get(profession) != null))
            );

        professionMap.forEach((key, value) -> row.add(newInlineCheckedKeyboardButton(key.getName(), key.getName(), value)));

        rows.add(row);
        rows.add(newInlineKeyboardButtonRow(CONFIRM, CONFIRM_USER_PROFESSION.getCommandName()));
        markupInline.setKeyboard(rows);

        messageSenderService.sendEditMessage(message.getChatId(), message.getText(), message.getMessageId(), markupInline);
    }
}

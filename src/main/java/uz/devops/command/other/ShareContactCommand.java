package uz.devops.command.other;

import static uz.devops.domain.enumeration.Command.SHARE_CONTACT;
import static uz.devops.utils.BotUtils.newKeyboardButton;
import static uz.devops.utils.MessageUtils.SHARE_CONTACT_FOR_REGISTRATION;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.command.Processor;
import uz.devops.service.MessageSenderService;

@RequiredArgsConstructor
@Service
public class ShareContactCommand implements Processor {

    private final MessageSenderService messageSenderService;

    @Override
    public void execute(Update update) {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton = newKeyboardButton(SHARE_CONTACT.getCommandName());
        keyboardButton.setRequestContact(true);
        keyboardRow.add(keyboardButton);
        keyboardRowList.add(keyboardRow);

        keyboardMarkup.setKeyboard(keyboardRowList);
        keyboardMarkup.setResizeKeyboard(true);

        messageSenderService.sendMessage(update.getMessage().getChatId(), SHARE_CONTACT_FOR_REGISTRATION, keyboardMarkup);
    }
}

package uz.devops.command.other;

import static uz.devops.domain.enumeration.Command.ABOUT_BOT;
import static uz.devops.domain.enumeration.Command.REGISTRATION;
import static uz.devops.utils.BotUtils.createKeyBoardRow;
import static uz.devops.utils.MessageUtils.CHOOSE_ONE_OF_THESE;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.command.Processor;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class StartCommand implements Processor {

    private final BotUtils botUtils;
    private final MessageSenderService messageSenderService;

    @Override
    public void execute(Update update) {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        if (botUtils.userIsEmpty(update.getMessage().getChatId())) {
            keyboardRowList.add(createKeyBoardRow(REGISTRATION));
        }

        keyboardRowList.add(createKeyBoardRow(ABOUT_BOT.getCommandName()));
        keyboardMarkup.setKeyboard(keyboardRowList);
        keyboardMarkup.setResizeKeyboard(true);

        messageSenderService.sendMessage(update.getMessage().getChatId(), CHOOSE_ONE_OF_THESE, keyboardMarkup);
    }
}

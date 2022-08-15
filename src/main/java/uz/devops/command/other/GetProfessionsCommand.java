package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Button;
import uz.devops.domain.Profession;
import uz.devops.repository.ButtonRepository;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service(Constants.GET_PROFESSIONS)
public class GetProfessionsCommand implements Processor {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private ProfessionRepository professionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ButtonRepository buttonRepository;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        String role = userRepository.findByChatId(chatId.toString()).get().getAuthorities().iterator().next().getName();
        String text = update.getMessage().getText();
        Optional<Button> optionalButton = buttonRepository.findByNameAndRole(text, role);
        if (optionalButton.isEmpty()){
            messageSenderService.sendMessage(chatId, "Noto'g'ri buyruq berildi!", null);
            return;
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<Profession> professionList = professionRepository.findAll();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Profession profession : professionList) {
            rows.add(BotUtils.newInlineKeyboardButtonRow(profession.getName(), " "));
        }
        InlineKeyboardButton inlineKeyboardButton1 = BotUtils.newInlineKeyboardButton(MessageUtils.ADD_NEW_PROFESSION, Constants.ADD_NEW_PROFESSION);
        InlineKeyboardButton inlineKeyboardButton2 = BotUtils.newInlineKeyboardButton(MessageUtils.REMOVE_PROFESSION, Constants.REMOVE_PROFESSION_COMMAND);
        List<InlineKeyboardButton> buttons=List.of(inlineKeyboardButton1, inlineKeyboardButton2);
        rows.add(buttons);
        keyboardMarkup.setKeyboard(rows);
        messageSenderService.sendMessage(update.getMessage().getChatId(), MessageUtils.CHOOSE_PROFESSION, keyboardMarkup);
    }
}

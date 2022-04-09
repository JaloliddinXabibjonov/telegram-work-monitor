package uz.devops.utils;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.domain.enumeration.Command.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.domain.User;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;

@Service
@RequiredArgsConstructor
public final class BotUtils {

    private final UserRepository userRepository;

    public Optional<User> findByChatId(Long chatId) {
        return userRepository.findByChatId(String.valueOf(chatId));
    }

    public boolean userIsEmpty(Long chatId) {
        return userRepository.findByChatId(String.valueOf(chatId)).isEmpty();
    }

    public String dateTimeFormatter(Instant date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        return date != null ? date.atZone(ZoneId.systemDefault()).toLocalDateTime().format(dateTimeFormatter) : "";
    }

    public Long getUserIdFromText(String text) {
        return Long.parseLong(text.substring(text.indexOf("#") + 1, text.indexOf("Ismi") - 2));
    }

    public Long getIdForSetRole(String text) {
        return Long.parseLong(text.substring(text.indexOf("#") + 1));
    }

    public Long getAnyIdFromText(String text) {
        return Long.parseLong(text.substring(text.indexOf("#") + 1, text.indexOf("\n")));
    }

    public Long getOrderIdFromText(String text) {
        return Long.parseLong(text.substring(text.indexOf("Buyurtma  #") + 11, text.indexOf("\n\n")));
    }

    // ------>  KEYBOARDS

    public ReplyKeyboardMarkup getMainReplyKeyboard(String chatId) {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        if (userRepository.findByChatId(chatId).isPresent() && !String.valueOf(chatId).equals(ADMIN_1_CHAT_ID)) {
            KeyboardRow userKeyboardRow1 = newKeyboardRow(MY_TASKS.getCommandName());
            userKeyboardRow1.add(EXIST_TASKS.getCommandName());

            KeyboardRow userKeyboardRow2 = newKeyboardRow(CALL_TO_ADMIN.getCommandName());
            userKeyboardRow2.add(MY_INFO.getCommandName());
            keyboardRowList.add(userKeyboardRow1);
            keyboardRowList.add(userKeyboardRow2);
        }

        if (Objects.equals(String.valueOf(chatId), ADMIN_1_CHAT_ID)) {
            KeyboardRow keyboardRow1 = newKeyboardRow(CREATE_NEW_JOB.getCommandName());
            keyboardRow1.add(CREATE_NEW_ORDER.getCommandName());
            //            keyboardRow1.add(CREATE_NEW_TASK.getCommandName());
            keyboardRowList.add(keyboardRow1);

            KeyboardRow keyboardRow2 = newKeyboardRow(REVIEW_JOB.getCommandName());
            //            keyboardRow2.add(REVIEW_TASK.getCommandName());
            keyboardRowList.add(keyboardRow2);
            //            keyboardRowList.add(newKeyboardRow(PIPELINE.getCommandName()));
        }

        keyboardMarkup.setKeyboard(keyboardRowList);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static KeyboardRow newKeyboardRow(String text) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(newKeyboardButton(text));
        return keyboardRow;
    }

    public static List<InlineKeyboardButton> newInlineKeyboardButtonRow(String text, String callbackData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(newInlineKeyboardButton(text, callbackData));
        return row;
    }

    public static KeyboardButton newKeyboardButton(String text) {
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(text);
        return keyboardButton;
    }

    public static InlineKeyboardButton newInlineKeyboardButton(String text, String callbackData) {
        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }

    public static InlineKeyboardButton newInlineCheckedKeyboardButton(String text, String callbackData, Boolean checked) {
        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(checked ? text + "  " + "☑" : text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }

    public ReplyKeyboardRemove keyboardRemove(Boolean isRemove) {
        var replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(isRemove);
        return replyKeyboardRemove;
    }

    public static InlineKeyboardMarkup newInlineKeyboardMarkup(List<InlineKeyboardButton> inlineKeyboardButtons) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(new ArrayList<>(inlineKeyboardButtons));
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public static InlineKeyboardMarkup getProfessionsKeyboard(ProfessionRepository professionRepository) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        professionRepository.findAll().forEach(profession -> row.add(newInlineKeyboardButton(profession.getName(), profession.getName())));

        rows.add(row);
        markupInline.setKeyboard(rows);
        return markupInline;
    }

    public ReplyKeyboardMarkup getBackKeyboard() {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        keyboardRowList.add(newKeyboardRow(BACK.getCommandName()));
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboardRowList);
        return keyboardMarkup;
    }
}

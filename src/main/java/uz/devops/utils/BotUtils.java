package uz.devops.utils;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.domain.enumeration.Command.*;
import static uz.devops.utils.Icons.EMPLOYEE_MAN_ICON;
import static uz.devops.utils.MessageUtils.TASK_DONE;
import static uz.devops.utils.MessageUtils.TASK_REJECTED;

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
import uz.devops.domain.enumeration.Command;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;

@Service
@RequiredArgsConstructor
public final class BotUtils {

    private final UserRepository userRepository;

    public boolean checkNumberValidation(String number) {
        String pattern = "\\d{1,10}";
        return number.matches(pattern);
    }

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

    public Long getTaskIdFromText(String text) {
        return Long.parseLong(text.substring(text.indexOf("Vazifa  #") + 9, text.indexOf("\n\n")));
    }

    public Long getOrderTaskId(String text) {
        return Long.parseLong(text.substring(text.indexOf("Info  #") + 7, text.indexOf("\n")));
    }

    // ------>  KEYBOARDS

    public ReplyKeyboardMarkup getMainReplyKeyboard(String chatId) {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        //        if (!Objects.equals(String.valueOf(chatId), ADMIN_1_CHAT_ID)) {
        keyboardRowList.add(createKeyBoardRow(MY_TASKS, EXIST_TASKS));
        keyboardRowList.add(createKeyBoardRow(CALL_TO_ADMIN, MY_INFO));
        //        }

        if (Objects.equals(String.valueOf(chatId), ADMIN_1_CHAT_ID)) {
            keyboardRowList.add(createKeyBoardRow(CREATE_NEW_JOB, CREATE_NEW_ORDER));
            keyboardRowList.add(createKeyBoardRow(REVIEW_JOB));
        }
        //        keyboardRowList.add(newKeyboardRow(PIPELINE.getCommandName()));
        //        keyboardRowList.add(newKeyboardRow(MENU.getCommandName()));
        keyboardMarkup.setKeyboard(keyboardRowList);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    private KeyboardRow createKeyBoardRow(Command myTasks, Command existTasks) {
        KeyboardRow userKeyboardRow1 = createKeyBoardRow(myTasks.getCommandName());
        userKeyboardRow1.add(existTasks.getCommandName());
        return userKeyboardRow1;
    }

    public static KeyboardRow createKeyBoardRow(Command command) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(newKeyboardButton(command.getCommandName()));
        return keyboardRow;
    }

    public static KeyboardRow createKeyBoardRow(String text) {
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

    public static InlineKeyboardMarkup getOrderKeyboard() {
        return newInlineKeyboardMarkup(
            List.of(newInlineKeyboardButton(GET_ORDER.getCommandName() + EMPLOYEE_MAN_ICON, GET_ORDER.getCommandName()))
        );
    }

    public InlineKeyboardMarkup getTaskOrderKeyboard() {
        return newInlineKeyboardMarkup(
            List.of(
                newInlineKeyboardButton(TASK_DONE, ORDER_DONE.getCommandName()),
                newInlineKeyboardButton(TASK_REJECTED, REJECT_ORDER.getCommandName())
            )
        );
    }

    public ReplyKeyboardMarkup getBackKeyboard() {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        keyboardRowList.add(createKeyBoardRow(BACK.getCommandName()));
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboardRowList);
        return keyboardMarkup;
    }
}

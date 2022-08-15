package uz.devops.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.command.ResultData;
import uz.devops.command.user.GetRoleFromUser;
import uz.devops.config.Constants;
import uz.devops.domain.*;
import uz.devops.domain.enumeration.Command;
import uz.devops.domain.enumeration.CommandStatus;
import uz.devops.domain.enumeration.RoleName;
import uz.devops.repository.ButtonRepository;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static uz.devops.WorkMonitorBot.ADMIN_1_CHAT_ID;
import static uz.devops.domain.enumeration.Command.*;
import static uz.devops.utils.Icons.EMPLOYEE_MAN_ICON;
import static uz.devops.utils.MessageUtils.TASK_DONE;
import static uz.devops.utils.MessageUtils.TASK_REJECTED;

@Service
@RequiredArgsConstructor
public final class BotUtils {

    private final UserRepository userRepository;

    @Autowired
    private GetRoleFromUser getRoleFromUser;
    @Autowired
    private ButtonRepository buttonRepository;
    @Autowired
    private ProfessionRepository professionRepository;

    public static final int PAGE = 0;
    public static final int SIZE = 10;

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
        keyboardRowList.add(createKeyBoardRow(MY_DOING_TASKS, MY_DONE_TASKS));
        keyboardRowList.add(createKeyBoardRow(CALL_TO_ADMIN, EXIST_TASKS));
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

    public KeyboardRow createKeyBoardRow2(Command myTasks, Command existTasks) {
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

    public static List<InlineKeyboardButton> newInlineKeyboardButtonRow(String text, String callBackData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(newInlineKeyboardButton(text, callBackData));
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

    public static List<InlineKeyboardButton> newInlineCheckedKeyboardButtonRow(String text, String callbackData, Boolean checked) {
        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(checked ? text + "  " + "☑" : text);
        inlineKeyboardButton.setCallbackData(callbackData);
        inlineKeyboardButtonList.add(inlineKeyboardButton);
        return inlineKeyboardButtonList;
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

    public InlineKeyboardMarkup newInlineKeyboardMarkup(String text, String callbackData) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(new ArrayList<>(createInlineKeyboardRow(text, callbackData)));
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }


    public static InlineKeyboardMarkup getProfessionsKeyboard(ProfessionRepository professionRepository, String startCallBackData) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Profession profession : professionRepository.findAll()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(newInlineKeyboardButton(profession.getName(), startCallBackData + profession.getName()));
            rows.add(row);
        }

        markupInline.setKeyboard(rows);
        return markupInline;
    }

    public static InlineKeyboardMarkup getOrderKeyboard(Long orderTaskId) {
        return newInlineKeyboardMarkup(
            List.of(newInlineKeyboardButton(GET_ORDER.getCommandName() + EMPLOYEE_MAN_ICON, Constants.GET_ORDER_TASK + "#" + orderTaskId))
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

    public InlineKeyboardMarkup getTaskOrderKeyboardByOrderTaskId(Long orderTaskId) {
        return newInlineKeyboardMarkup(
            List.of(
                newInlineKeyboardButton(TASK_DONE, Command.TASK_DONE.getCommandName() + "#" + orderTaskId),
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

    public ReplyKeyboardMarkup createMainButtonsByRole(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        Optional<User> optionalUser = userRepository.findByChatIdAndConfirmedTrue(chatId.toString());
        String role = "";
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (getRoleFromUser.checkUser(user)) {
                role = RoleName.ISHCHI.toString();
            } else if (getRoleFromUser.checkClientFromMessage(user)) {
                role = RoleName.BUYURTMACHI.toString();
            } else {
                role = RoleName.ADMIN.toString();
            }
        }
        List<Button> buttonSet = buttonRepository.getAllByStatusAndRole(CommandStatus.MAIN_BUTTON, role);
        for (int i = 0; i < buttonSet.size(); i++) {
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(buttonSet.get(i).getName());
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(keyboardButton);
            keyboardRowList.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }

    private List<KeyboardRow> buttonHelper(Set<Button> buttons, List<KeyboardRow> keyboardRowList) {
        for (Button button : buttons) {
            createRow(keyboardRowList, button);
        }
        return keyboardRowList;
    }


    public ReplyKeyboardMarkup createMainButtonForClient() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        List<Button> buttonSet = buttonRepository.getAllByStatusAndRole(CommandStatus.MAIN_BUTTON, RoleName.BUYURTMACHI.toString());
        for (Button button : buttonSet) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(button.getName());
            keyboardRowList.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }


    private void createRow(List<KeyboardRow> keyboardRowList, Button button) {
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(button.getName());
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(keyboardButton);
        keyboardRowList.add(keyboardRow);
    }


    public List<InlineKeyboardButton> createInlineKeyboardRow(String text, String data) {
        List<InlineKeyboardButton> inlineKeyboardButtonRow = new ArrayList<>();
        inlineKeyboardButtonRow.add(createInlineKeyboardButton(text, data));
        return inlineKeyboardButtonRow;
    }

    public InlineKeyboardButton createInlineKeyboardButton(String text, String data) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(data);
        return inlineKeyboardButton;
    }

    public ResultData createMyDoneTasksByPageable(List<OrderTask> orderTasks, Integer page) {
        String message = "";
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonRow1 = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtonRow2 = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonRow3 = new ArrayList<>();
        for (int i = 1; i <= orderTasks.size(); i++) {
            OrderTask orderTask = orderTasks.get(i - 1);
            InlineKeyboardButton inlineKeyboardButton = BotUtils.newInlineKeyboardButton(Integer.toString(i), Constants.GET_ORDER_TASK_BY_ID + "#" + orderTask.getId().toString());
            if (orderTasks.size() > 6) {
                if (i > orderTasks.size() / 2) {
                    inlineKeyboardButtonRow2.add(inlineKeyboardButton);
                } else {
                    inlineKeyboardButtonRow1.add(inlineKeyboardButton);
                }
            } else {
                inlineKeyboardButtonRow2.add(inlineKeyboardButton);
            }
            message = message.concat(i + ". " + orderTask.getTask().getName() + " | " + dateTimeFormatter(orderTask.getEndDate()) + "\n");
        }
        rowList.add(inlineKeyboardButtonRow1);
        rowList.add(inlineKeyboardButtonRow2);
        InlineKeyboardButton nextPageButton = BotUtils.newInlineKeyboardButton(Icons.NEXT_PAGE_ICON, GET_PAGE_OF_MY_DONE_TASKS.getCommandName() + "#" + (page + 1));
        InlineKeyboardButton previousPageButton = BotUtils.newInlineKeyboardButton(Icons.PREVIOUS_PAGE_ICON, GET_PAGE_OF_MY_DONE_TASKS.getCommandName() + "#" + (page - 1));
        InlineKeyboardButton deleteMessageButton = BotUtils.newInlineKeyboardButton(Icons.REMOVE_ICON, Constants.DELETE_MESSAGE);
        inlineKeyboardButtonRow3.add(previousPageButton);
        inlineKeyboardButtonRow3.add(deleteMessageButton);
        inlineKeyboardButtonRow3.add(nextPageButton);
        rowList.add(inlineKeyboardButtonRow3);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new ResultData(message, inlineKeyboardMarkup);
    }

    public Boolean checkUserForAdminOrCustomer(User user) {
        for (Authority authority : user.getAuthorities()) {
            return authority.getName().equals(RoleName.ADMIN.toString()) || authority.getName().equals(RoleName.BUYURTMACHI.toString());
        }
        return false;
    }
}

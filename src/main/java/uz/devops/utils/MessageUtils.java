package uz.devops.utils;

import static uz.devops.domain.enumeration.Command.ASSIGN_ROLE_TO_USER;
import static uz.devops.utils.Icons.*;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.devops.domain.Job;
import uz.devops.domain.Order;
import uz.devops.domain.Task;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.OrderStatus;

@Service
public class MessageUtils {

    private final BotUtils botUtils;

    Map<OrderStatus, String> taskStatusStringMap;

    public MessageUtils(BotUtils botUtils) {
        this.botUtils = botUtils;
        taskStatusStringMap =
            ImmutableMap
                .<OrderStatus, String>builder()
                .put(OrderStatus.TO_DO, PURPLE_ROUND_ICON)
                .put(OrderStatus.DOING, BLUE_ROUND_ICON)
                .put(OrderStatus.DONE, CONFIRM_ICON)
                //                .put(TaskStatus.PAUSED, ORANGE_ROUND_ICON)
                //                .put(TaskStatus.REJECTED, GREEN_ROUND_ICON)
                .build();
    }

    public static final String MESSAGE_TO_USER_ON_REG =
        "Sizning ma'lumotlaringiz adminga yuborildi. Admin sizni tasdiqlagandan so'ng botdan foydalanishingiz mumkin. Iltimos kuting !";
    public static final String CONFIRMATION_MESSAGE =
        "\uD83C\uDF89 \uD83C\uDF89     Tabriklaymiz !!!     \uD83C\uDF89 \uD83C\uDF89" +
        "\n" +
        "Siz tasdiqlangan foydalanuvchilar safiga qo'shildingiz endi botdan to'liq foydalanishingiz mumkin .";
    public static final String REJECTED_USER_MESSAGE =
        "Admin sizni tasdiqlamadi  \uD83D\uDE14" + "\n" + "Keyinroq qaytadan urinib ko'ring .";
    public static final String USER_REMOVED = "Foydalanuvchi o'chirildi  " + CONFIRM_ICON;
    public static final String JOB_REMOVED = "Ish o'chirildi  " + CONFIRM_ICON;
    public static final String TASK_REMOVED = "Vazifa o'chirildi  " + CONFIRM_ICON;
    public static final String CONFIRM = "Tasdiqlash  ✅";
    public static final String TASK_CREATED = "Vazifa yaratildi " + CONFIRM_ICON;
    public static final String JOB_CREATED = "Ish yaratildi " + CONFIRM_ICON;
    public static final String ROLE_NOT_FOUND = "Xech qanday role topilmadi";
    public static final String ADD_TASK_TO_JOB = "Ish uchun vazifa qo'shing  ❗" + "\n" + "Vazifa nomini kiriting";
    public static final String USER_CONFIRMED = "Foydalanuvchi tasdiqlandi  " + CONFIRM_ICON;
    public static final String REGISTERED_NEW_USER = "Yangi foydalanuvchi ro'yxatdan o'tdi ⚡";
    public static final String USER_WORK_NOT_FOUND = "Sizda ishlar mavjud emas";
    public static final String HELP_MESSAGE =
        "Ushbu bot sizga xodimlaringizning ishlarini boshqarishda va ularni nazorat qilib borishda yordam beradi." +
        " Undan to'liq foydalanishingiz uchun avval ro'yxatdan o'ting .";
    public static final String CHOOSE_ONE_OF_THESE = "Quyidagilardan birini tanlang";
    public static final String DOING_WORKS_NOT_FOUND = "Bajarilayotgan ishlar mavjud emas";
    public static final String SHARE_CONTACT_FOR_REGISTRATION = "Ro'yhatdan o'tish uchun kontaktingizni ulashing";
    public static final String NO_WORKS_YET = "Hozircha ishlar mavjud emas";
    public static final String UNKNOWN_MESSAGE = "Noto'g'ri xabar kiritildi";
    public static final String CHOOSE_LANG = "Tilni tanlang";
    public static final String SET_TASK_JOB = "Vazifa  ➕";
    public static final String SET_TASK_TO_JOB = "Ish uchun xizmat birikitiring \n  ";
    public static final String EDIT_NAME = "Nomni o'zgartirish  ";
    public static final String EDIT_PRICE = "Narxni o'zgartirish  ";
    public static final String EDIT_STATUS = "Statusni o'zgartirish  ";
    public static final String EDIT_JOB_ = "Ishni tahrirlash";
    public static final String TASK_NAME_MODIFIED = "Vazifa nomi o'zgartirildi " + CONFIRM_ICON;
    public static final String TASK_PRICE_MODIFIED = "Vazifa narxi o'zgartirildi " + CONFIRM_ICON;
    public static final String TASK_DONE = "Vazifa bajarildi  " + CONFIRM_ICON;
    public static final String JOB_NAME_EDITED = "Ish nomi o'zgartirildi  " + CONFIRM_ICON;
    public static final String TASK_REJECTED = "Rad etish  " + REJECT_MAN_ICON;
    public static final String ENTER_NEW_NAME = "Yangi nom kiriting \n";
    public static final String ENTER_NEW_PRICE = "Yangi narx kiriting \n";
    public static final String WORK_GOT = "Afsus ish olib bo'lindi ulgurmadiz  ☹";
    public static final String TASK_REJECTED_BY_USER = "Ish rad etildi  ☹";

    public String getJobInfo(Job job) {
        return "<b>Ish   #</b>" + job.getId() + " uchun vazifa qo'shing  ❗" + "\n" + "\n" + "Vazifa nomini kiriting";
    }

    public String getUserInfo(User user) {
        return (
            "<b>User   #</b>" +
            user.getId() +
            "\n" +
            "\n" +
            "Ism:  " +
            user.getFirstName() +
            "\n" +
            "Username:  " +
            user.getTgUsername() +
            "\n" +
            "Telefon raqam:  " +
            user.getPhoneNumber() +
            "\n" +
            "Ro'yhatdan o'tgan sana:  " +
            botUtils.dateTimeFormatter(user.getCreatedDate())
        );
    }

    public String getAboutMyTask(Order order) {
        return (
            "<b>Task  #</b>" +
            order.getId() +
            "\n" +
            "<b>Buyurtma  #</b>" +
            order.getId() +
            "\n" +
            "\n" +
            "\uD83D\uDCB0  " +
            order.getPrice() +
            "\n" +
            "\uD83D\uDCC6  " +
            botUtils.dateTimeFormatter(order.getStartedDate()) +
            "  -  " +
            botUtils.dateTimeFormatter(order.getEndDate()) +
            "\n" +
            "\uD83D\uDCDC  " +
            order.getDescription() +
            "\n" +
            EMPLOYEE_MAN_ICON +
            "  @" +
            order.getEmployee() +
            "\n" +
            "Status:  " +
            "<b><i>" +
            order.getStatus() +
            "</i></b>" +
            "  " +
            taskStatusStringMap.get(order.getStatus())
        );
    }

    public String getTaskInfo(Order order) {
        return (
            "<b>Buyurtma  #</b>" +
            order.getId() +
            "\n" +
            "\n" +
            //                "\uD83D\uDCB0  " + order.getPrice() + "\n" +
            "\uD83D\uDCDC  " +
            order.getDescription()
        );
    }

    public String testTaskInfo(Order order) {
        return (
            "<b>Buyurtma  #</b>" +
            order.getId() +
            "\n" +
            "\n" +
            "\uD83D\uDCB0  " +
            order.getPrice() +
            "\n" +
            "\uD83D\uDCC6  " +
            botUtils.dateTimeFormatter(order.getStartedDate()) +
            "\n" +
            "\uD83D\uDCDC  " +
            order.getDescription() +
            "\n" +
            EMPLOYEE_MAN_ICON +
            "  @" +
            order.getEmployee() +
            "\n" +
            "Status:  " +
            "<b><i>" +
            order.getStatus() +
            "</i></b>" +
            "  " +
            taskStatusStringMap.get(order.getStatus())
        );
    }

    // NOTIFICATION FOR ADMIN

    public String getTask(Order order) {
        return (
            "<b>Buyurtma  #</b>" +
            order.getId() +
            "\n" +
            "\n" +
            "Nomi:  " +
            order.getName() +
            "\n" +
            "Status:  " +
            order.getStatus() +
            "\n" +
            "Narxi:  " +
            order.getPrice() +
            "\n" +
            "Ish olingan sana:  " +
            botUtils.dateTimeFormatter(order.getStartedDate()) +
            "\n" +
            "Bajaruvchi:  @" +
            order.getEmployee()
        );
    }

    public String getTaskInfoAfterTook(Order order) {
        return (
            "Ish olindi  ⚡" +
            "\n" +
            "\n" +
            "\uD83D\uDCB0  " +
            order.getPrice() +
            "\n" +
            "\uD83D\uDCDC  " +
            order.getDescription() +
            "\n" +
            "\uD83D\uDCC6  " +
            botUtils.dateTimeFormatter(order.getStartedDate()) +
            "\n" +
            EMPLOYEE_MAN_ICON +
            "  @" +
            order.getEmployee() +
            "\n" +
            "Status:  " +
            "<b><i>" +
            order.getStatus() +
            "</i></b>" +
            "  " +
            taskStatusStringMap.get(order.getStatus())
        );
    }

    public String getRegisteredNewUserInfo(User user) {
        return (
            REGISTERED_NEW_USER +
            "\n" +
            "<b>User   #</b>" +
            user.getId() +
            "\n" +
            "\n" +
            "Ismi:  " +
            user.getFirstName() +
            "\n" +
            "Telefon raqami:  " +
            user.getPhoneNumber() +
            "\n" +
            "Telegram username:  @" +
            user.getTgUsername() +
            "\n" +
            "Ro'yhatdan o'tgan sana:  " +
            botUtils.dateTimeFormatter(user.getCreatedDate())
        );
    }

    public String rejectTask(Order order, Message message) {
        return (
            "Buyurtma  #" +
            order.getId() +
            "\n" +
            "\n" +
            order.getDescription() +
            "\n" +
            "@" +
            message.getChat().getUserName() +
            " tomonidan rad etildi  ☹"
        );
    }

    public String taskDoneForAdmin(Order order) {
        return "<b>Buyurtma  #</b>" + order.getId() + " bajarildi  ⚡" + "\n" + EMPLOYEE_MAN_ICON + "  @" + order.getEmployee();
    }

    public String assignRoleToUser(Long parsedUserId) {
        return ASSIGN_ROLE_TO_USER.getCommandName() + "\n" + "#" + parsedUserId + "\n";
    }

    public String getJobs(Job job) {
        return "Ish   #" + job.getId() + "\n" + "<b>" + job.getName() + "</b>" + getJobTasksToEdit("", job.getTasks());
    }

    public String getJobTasksToEdit(String message, Set<Task> tasks) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : tasks) {
            stringBuilder.append("<b>").append(task.getId()).append("</b>").append(". ").append(task.getName()).append("\n");
        }
        return message + "\n" + "\n" + "<i>Vazifalar</i>: " + "\n" + stringBuilder;
    }

    public String createOrderForUsers(Order order) {
        return (
            "Ish   #" +
            order.getId() +
            "  ⚡" +
            "\n" +
            "\n" +
            "\uD83D\uDCB0  " +
            order.getPrice() +
            "\n" +
            "\uD83D\uDCDC  " +
            order.getDescription()
        );
    }
}

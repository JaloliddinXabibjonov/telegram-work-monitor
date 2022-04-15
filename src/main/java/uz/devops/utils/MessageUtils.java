package uz.devops.utils;

import static uz.devops.domain.enumeration.Command.ASSIGN_ROLE_TO_TASK;
import static uz.devops.domain.enumeration.Command.ASSIGN_ROLE_TO_USER;
import static uz.devops.utils.Icons.*;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.devops.domain.*;
import uz.devops.domain.enumeration.Status;

@Service
public class MessageUtils {

    private final BotUtils botUtils;

    Map<Status, String> taskStatusStringMap;

    public MessageUtils(BotUtils botUtils) {
        this.botUtils = botUtils;
        taskStatusStringMap =
            ImmutableMap
                .<Status, String>builder()
                .put(Status.TO_DO, PURPLE_ROUND_ICON)
                .put(Status.NEW, PURPLE_ROUND_ICON)
                .put(Status.DOING, BLUE_ROUND_ICON)
                .put(Status.DONE, CONFIRM_ICON)
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
    public static final String USER_NOT_FOUND = "Foydalanuvchi topilmadi !";
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
    public static final String CREATE_TASK = "Vazifa  ➕";
    public static final String CREATE_ORDER = "Buyurtma  ➕";
    public static final String EDIT_NAME = "Nomni o'zgartirish  ";
    public static final String TASK_NAME_MODIFIED = "Vazifa nomi o'zgartirildi " + CONFIRM_ICON;
    public static final String TASK_PRICE_MODIFIED = "Vazifa narxi o'zgartirildi " + CONFIRM_ICON;
    public static final String TASK_DONE = "Vazifa bajarildi  " + CONFIRM_ICON;
    public static final String JOB_NAME_EDITED = "Ish nomi o'zgartirildi  " + CONFIRM_ICON;
    public static final String TASK_REJECTED = "Rad etish  " + REJECT_MAN_ICON;
    public static final String ENTER_NEW_NAME = "Yangi nom kiriting \n";
    public static final String ENTER_NEW_PRICE = "Yangi narx kiriting \n";
    public static final String WORK_GOT = "Afsus ish olib bo'lindi ulgurmadiz  ☹";
    public static final String SERVER_ERROR = "Server bilan xatolik yuzaga keldi  ☹";
    public static final String ONLY_NUMBER = "Faqat raqam kiriting";
    public static final String JOB = "Ish  \uD83D\uDD8C";
    public static final String TASK = "Vazifa  \uD83D\uDD8C";
    public static final String ENTER_DESC_ORDER = "Buyurtma haqida ma'lumot kiriting";
    public static final String CREATED_NEW_TASK = "Yangi vazifa yaratildi  ⚡";
    public static final String ONLY_ENTER_NUMBER = "Narxni faqat raqamlarda kiriting !";
    public static final String ENTER_TASK_DESCRIPTION = "Vazifa haqida ma'lumot kiriting";

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

    public String getAboutMyTask(OrderTask orderTask, Task task) {
        return (
            "<b>Info  #</b>" +
            orderTask.getId() +
            "\n" +
            "\n" +
            task.getName() +
            "\n" +
            "\uD83D\uDCB0  " +
            task.getPrice() +
            "\n" +
            "\uD83D\uDCC6  " +
            botUtils.dateTimeFormatter(orderTask.getStartedDate()) +
            "  -  " +
            botUtils.dateTimeFormatter(orderTask.getEndDate()) +
            "\n" +
            "\uD83D\uDCDC  " +
            task.getDescription() +
            "\n" +
            EMPLOYEE_MAN_ICON +
            "  @" +
            orderTask.getEmployeeUsername() +
            "\n" +
            "Status:  " +
            "<b><i>" +
            orderTask.getStatus() +
            "</i></b>" +
            "  " +
            taskStatusStringMap.get(orderTask.getStatus())
        );
    }

    public String getTaskInfo(Long orderTaskId, Order order, Task task) {
        return (
            "<b>Info  #</b>" +
            orderTaskId +
            "\n" +
            "<b>Vazifa  #</b>" +
            task.getId() +
            "\n" +
            "\n" +
            task.getName() +
            "\n" +
            "\uD83D\uDCB0  " +
            task.getPrice() +
            "\n" +
            "\uD83D\uDCDC  " +
            task.getDescription() +
            "\n" +
            "Takrorlash:  " +
            order.getCount() +
            " ta" +
            "\n" +
            "Qo'shimcha ma'lumot:  " +
            order.getDescription()
        );
    }

    public String myTask(Task task, OrderTask orderTask) {
        return (
            "<b>Info  #</b>" +
            orderTask.getId() +
            "\n" +
            "<b>Vazifa  #</b>" +
            task.getId() +
            "\n" +
            "\n" +
            task.getName() +
            "\n" +
            "\uD83D\uDCB0  " +
            task.getPrice() +
            "\n" +
            "\uD83D\uDCC6  " +
            botUtils.dateTimeFormatter(orderTask.getStartedDate()) +
            "\n" +
            "\uD83D\uDCDC  " +
            task.getDescription() +
            "\n" +
            EMPLOYEE_MAN_ICON +
            "  @" +
            orderTask.getEmployeeUsername() +
            "\n" +
            "Status:  " +
            "<b><i>" +
            orderTask.getStatus() +
            "</i></b>" +
            "  " +
            taskStatusStringMap.get(orderTask.getStatus())
        );
    }

    // NOTIFICATION FOR ADMIN

    public String getTaskInfoAfterTook(Task task, OrderTask orderTask) {
        return (
            "Ish olindi  ⚡" +
            "\n" +
            "\n" +
            task.getName() +
            "\n" +
            "\uD83D\uDCB0  " +
            task.getPrice() +
            "\n" +
            "\uD83D\uDCDC  " +
            task.getDescription() +
            "\n" +
            "\uD83D\uDCC6  " +
            botUtils.dateTimeFormatter(orderTask.getStartedDate()) +
            "\n" +
            EMPLOYEE_MAN_ICON +
            "  @" +
            orderTask.getEmployeeUsername() +
            "\n" +
            "Status:  " +
            "<b><i>" +
            orderTask.getStatus() +
            "</i></b>" +
            "  " +
            taskStatusStringMap.get(orderTask.getStatus())
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

    public String rejectTask(Order order, Task task, Message message) {
        return (
            "Buyurtma  #" +
            order.getId() +
            "\n" +
            "\n" +
            task.getName() +
            "\n" +
            "@" +
            message.getChat().getUserName() +
            " tomonidan rad etildi  ☹"
        );
    }

    public String taskDoneForAdmin(Long orderId, String username) {
        return "<b>Buyurtma  #</b>" + orderId + " bajarildi  ⚡" + "\n" + EMPLOYEE_MAN_ICON + "  @" + username;
    }

    public String assignRoleToUser(Long parsedUserId) {
        return ASSIGN_ROLE_TO_USER.getCommandName() + "\n" + "#" + parsedUserId + "\n";
    }

    public String getJobs(Job job, List<Task> tasks) {
        return "Ish   #" + job.getId() + "\n" + "<b>" + job.getName() + "</b>" + "\n" + getJobTasks(tasks);
    }

    private String getJobTasks(List<Task> tasks) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : tasks) {
            stringBuilder.append("<b>").append(task.getId()).append("</b>").append(". ").append(task.getName()).append("\n");
        }
        return "\n" + "<i>Vazifalar</i>: " + "\n" + stringBuilder;
    }

    public String createNewOrder(Long orderId) {
        return "Yangi buyurtma  #" + orderId + " yaratildi   ⚡";
    }

    public String jobCompleted(String jobName) {
        return jobName + " bo'yicha vazifalar tugatildi  ⚡";
    }

    public String enterOrderCount() {
        return "Buyurtmalar sonini kiriting" + "\n" + "<i>Namuna:</i>  3";
    }

    public String addNewTask(Long id) {
        return "Ish  #" + id + "\n" + "Yana vazifa qo'shmoqchimisiz ?";
    }

    public String exampleEnterPrice() {
        return "Narxini kiriting" + "\n" + "<i>Namuna:</i>  " + "755000";
    }

    public String setRoleToTask(Long id) {
        return ASSIGN_ROLE_TO_TASK.getCommandName() + "\n" + "\n" + "Task  #" + id;
    }

    public String existsTasks(List<Task> tasks) {
        return "" + getJobTasks(tasks);
    }
}

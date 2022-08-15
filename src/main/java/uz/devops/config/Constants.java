package uz.devops.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String TELEGRAM_BOT_URL = "https://api.telegram.org/bot";
    public static final String TELEGRAM_BOT_TOKEN = "5385084898:AAETJ9tZSxppYKVrd5JXaorWHNLYEFFb6-Q";
    public static final String CHOOSE_ROLE="CHOOSE_ROLE";
    public static final String SET_PROFESSION_TO_USER = "set_profession_to_user";
    public static final String SET_ROLE_TO_USER = "set_role_to_user";
    public static final String CONFIRM_ORDER = "confirm_order";
    public static final String TASK_DONE = "task_done";
    public static final String ADD_TASK_TO_JOB = "add_task_to_job";
    public static final String ADD_NEW_PROFESSION = "add_new_profession";
    public static final String SAVE_NEW_PROFESSION = "save_new_profession";
    public static final String REMOVE_PROFESSION_COMMAND = "profession_remove";
    public static final String REMOVE_PROFESSION_BY_NAME = "remove_profession_by_name";
    public static final String REMOVE_USER = "remove_user";
    public static final String EDIT_JOB = "Ishni tahrirlash \uD83D\uDD8C";
    public static final String CHOOSE_ONE_TO_EDIT = "choose_one_to_edit";
    public static final String REMOVE_TASK = "remove_task";
    public static final String CHOOSE_TASK_TO_EDIT = "choose_task_to_edit"  ;
    public static final String DELETE_MESSAGE = "delete_message";

    private Constants() {}


    public static final String EXIST_TASKS="Hozirda mavjud ishlar  \uD83E\uDDF0";
    public static final String CREATE_NEW_TASK="Yangi vazifa yaratish \uD83D\uDEE0";
    public static final String CONFIRM_TASK_PROFESSION="confirm_task_profession";
    public static final String CREATE_NEW_JOB="Yangi ish yaratish \uD83E\uDDF0";
    public static final String REJECT_ORDER="reject_order";
    public static final String CREATE_NEW_ORDER="Buyurtma yaratish \uD83D\uDEE0";
    public static final String START="/start";
    public static final String MY_DOING_TASKS="Mening bajarayotgan ishlarim  \uD83D\uDD28";
    public static final String MY_DONE_TASKS="Mening bajargan ishlarim  \uD83D\uDD28";
    public static final String CALL_TO_ADMIN="Admin bilan aloqa  \uD83D\uDCDE";
    public static final String ADD_NEW_USER="Yangi foydalanuvchi qo'shish \uD83D\uDD16";
    public static final String SET_PROFESSION_TO_TASK="set_profession_to_task";
    public static final String CONFIRM_PROFESSION="confirm_profession";
    public static final String GET_PAGE_OF_MY_DONE_TASKS="get_page";
    public static final String GET_ORDER_TASK  ="take_order_task";
    public static final String GET_ORDER_TASK_BY_ID  ="get_order_task_by_id";
    public static final String GET_MAIN_KEYBOARDS  ="get_main_keyboards";
    public static final String GET_PROFESSIONS ="Kasblar \uD83D\uDC68\u200D\uD83D\uDD27";
    public static final String ADD_PROFESSION ="add_profession";
    public static final String ADD_ORDER_BY_SHORT_COMMAND ="#order";
    public static final String REPORT ="Hisobot \uD83D\uDCD2";
    public static final String SALARY ="Ish haqi to'g'risida ma'lumot\uD83D\uDCB0";
}

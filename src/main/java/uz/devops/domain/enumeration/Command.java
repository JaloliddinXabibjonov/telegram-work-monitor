package uz.devops.domain.enumeration;

import lombok.Getter;

@Getter
public enum Command {
    START("/start"),
    STOP("/stop"),
    EMPLOYEES("/employees"),
    SETTINGS("/settings"),

    ABOUT_BOT("Bot haqida  ℹ"),
    CALL_TO_ADMIN("Admin bilan aloqa  \uD83D\uDCDE"),
    EXIST_TASKS("Hozirda mavjud ishlar  \uD83E\uDDF0"),
    EXIST_JOBS("Hozirda mavjud ishlar"),
    REVIEW_JOB("Ishni tahrirlash \uD83D\uDD8C"),
    REVIEW_TASK("Vazifani tahrirlash \uD83D\uDD8A"),
    CREATE_NEW_PROFESSION("Yangi kasb"),
    USERS("/users"),
    SET_ROLE_TO_USER("set_role_to_user"),
    CONFIRM_USER_PROFESSION("confirm_user_profession"),
    CONFIRM_ORDER_PROFESSION("confirm_order_profession"),
    CONFIRM_ORDER("confirm_order"),
    CONFIRM_TASK_PROFESSION("confirm_task_profession"),
    CONFIRM_JOB_TASK("confirm_job_task"),
    REMOVE_USER("remove_user"),
    REGISTRATION("Ro'yhatdan o'tish \uD83D\uDCDD"),
    SHARE_CONTACT("Kontaktni ulashish ☎"),
    ASSIGN_ROLE_TO_USER("Foydalanuvchi uchun role biriktiring \uD83D\uDCCC"),
    ASSIGN_ROLE_TO_TASK("Task uchun role biriktiring \uD83D\uDCCC"),

    MY_INFO("Mening ma'lumotlarim  ℹ"),
    MY_TASKS("Mening ishlarim  \uD83D\uDD28"),
    PIPELINE("Pipeline"),
    GET_MAIN_KEYBOARDS("get_main_keyboards"),
    GET_TASK("Ishni olish"),
    GET_ORDER("Ishni olish"),
    EDIT_JOB("edit_job"),
    CREATE_ORDER("create_order"),
    CHOOSE_ONE_TO_EDIT("choose_one_to_edit"),
    EDIT_JOB_NAME("edit_job_name"),
    EDIT_TASK_NAME("edit_task_name"),
    EDIT_TASK_PRICE("edit_task_price"),
    EDIT_JOB_STATUS("edit_job_status"),
    EDIT_TASK("edit_task"),
    CHOOSE_TASK_TO_EDIT("choose_task_to_edit"),
    TASK_DONE_COMMAND("task_done"),
    ORDER_DONE("order_done"),
    REJECT_TASK("reject_task"),
    REJECT_ORDER("reject_order"),
    SET_TASK_TO_JOB("set_task_to_job"),
    REMOVE_JOB("remove_job"),
    REMOVE_TASK("remove_task"),
    UZ("Uz"),
    RU("Ru"),
    ADD_TASK_TO_JOB("add_task_to_job"),
    CREATE_NEW_TASK("Yangi vazifa yaratish \uD83D\uDEE0"),
    CREATE_NEW_ORDER("Buyurtma yaratish \uD83D\uDEE0"),
    CREATE_NEW_JOB("Yangi ish yaratish \uD83E\uDDF0"),
    ENTER_TASK_NAME("Vazifa uchun nom kiriting"),
    ENTER_JOB_NAME("Ish nomini kiriting"),
    ENTER_PRICE("Narxini kiriting"),
    ENTER_TASK_DESCRIPTION("Vazifa haqida ma'lumot kiriting"),
    ENTER_PERIOD("Taxminiy sarflanadigan vaqtni kiriting"),
    RU_SELECTED("Ru"),
    BACK("Orqaga");

    private final String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }
}

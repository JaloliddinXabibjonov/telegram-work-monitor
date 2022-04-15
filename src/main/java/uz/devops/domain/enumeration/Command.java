package uz.devops.domain.enumeration;

import lombok.Getter;

@Getter
public enum Command {
    EDIT_TASK("edit_task"),
    REMOVE_TASK("remove_task"),
    EXIST_TASKS("Hozirda mavjud ishlar  \uD83E\uDDF0"),
    EDIT_TASK_NAME("edit_task_name"),
    ENTER_TASK_NAME("Vazifa nomini kiriting"),
    EDIT_TASK_PRICE("edit_task_price"),
    CREATE_NEW_TASK("Yangi vazifa yaratish \uD83D\uDEE0"),
    CHOOSE_TASK_TO_EDIT("choose_task_to_edit"),
    CONFIRM_TASK_PROFESSION("confirm_task_profession"),

    EDIT_JOB("edit_job"),
    REMOVE_JOB("remove_job"),
    REVIEW_JOB("Ishni tahrirlash \uD83D\uDD8C"),
    EDIT_JOB_NAME("edit_job_name"),
    ENTER_JOB_NAME("Ish nomini kiriting"),
    CREATE_NEW_JOB("Yangi ish yaratish \uD83E\uDDF0"),
    ADD_TASK_TO_JOB("add_task_to_job"),

    REMOVE_USER("remove_user"),
    REGISTRATION("Ro'yhatdan o'tish \uD83D\uDCDD"),
    SET_ROLE_TO_USER("set_role_to_user"),
    ASSIGN_ROLE_TO_TASK("Task uchun role biriktiring \uD83D\uDCCC"),
    ASSIGN_ROLE_TO_USER("Foydalanuvchi uchun role biriktiring \uD83D\uDCCC"),
    CONFIRM_USER_PROFESSION("confirm_user_profession"),

    GET_ORDER("Ishni olish"),
    ORDER_DONE("order_done"),
    REJECT_ORDER("reject_order"),
    CREATE_ORDER("create_order"),
    CREATE_NEW_ORDER("Buyurtma yaratish \uD83D\uDEE0"),
    CHOOSE_ONE_TO_EDIT("choose_one_to_edit"),

    BACK("Orqaga"),
    MENU("Menu"),
    START("/start"),
    MY_INFO("Mening ma'lumotlarim  ℹ"),
    MY_TASKS("Mening ishlarim  \uD83D\uDD28"),
    ABOUT_BOT("Bot haqida  ℹ"),
    SHARE_CONTACT("Kontaktni ulashish ☎"),
    CALL_TO_ADMIN("Admin bilan aloqa  \uD83D\uDCDE"),
    GET_MAIN_KEYBOARDS("get_main_keyboards");

    private final String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }
}

package uz.devops.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.devops.domain.Authority;
import uz.devops.domain.Button;
import uz.devops.domain.Profession;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.CommandStatus;
import uz.devops.domain.enumeration.RoleName;
import uz.devops.repository.AuthorityRepository;
import uz.devops.repository.ButtonRepository;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.UserRepository;

import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ButtonRepository buttonRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private UserRepository userRepository;
    @Value("${spring.sql.init.mode}")
    private String mode;

    @Override
    public void run(String... args) throws Exception {
        if (mode.equals("always")) {
            Profession profession = professionRepository.save(new Profession("Polirovkachi"));
            professionRepository.save(new Profession("Bo'yoqchi"));
            professionRepository.save(new Profession("Yetkazib beruvchi"));
            authorityRepository.save(new Authority(RoleName.ISHCHI.toString()));
            authorityRepository.save(new Authority(RoleName.BUYURTMACHI.toString()));
            authorityRepository.save(new Authority(RoleName.ADMIN.toString()));
            userRepository.save(new User(true, "+998993636224", true, Collections.singleton(new Authority("ADMIN"))));
//            userRepository.save(new User(true, "+998913657491", true, Collections.singleton(new Authority(RoleName.BUYURTMACHI.toString()))));
//            userRepository.save(new User(true, "+998333636224", true, Collections.singleton(profession), Collections.singleton(new Authority(RoleName.ISHCHI.toString()))));
            buttonRepository.save(new Button("/start", CommandStatus.START, CommandStatus.START.toString()));
            buttonRepository.save(new Button("Hozirda mavjud ishlar  \uD83E\uDDF0", CommandStatus.MAIN_BUTTON, RoleName.ISHCHI.toString(), CommandStatus.EXIST_TASKS.toString()));
            buttonRepository.save(new Button("Mening bajargan ishlarim  \uD83D\uDD28", CommandStatus.MAIN_BUTTON, RoleName.ISHCHI.toString(), CommandStatus.MY_DONE_TASKS.toString()));
            buttonRepository.save(new Button("Mening bajarayotgan ishlarim  \uD83D\uDD28", CommandStatus.MAIN_BUTTON, RoleName.ISHCHI.toString(), CommandStatus.MY_DOING_TASKS.toString()));
            buttonRepository.save(new Button("Admin bilan aloqa  \uD83D\uDCDE", CommandStatus.MAIN_BUTTON, CommandStatus.CALL_TO_ADMIN.toString()));
            buttonRepository.save(new Button("Ishni olish", CommandStatus.GET_ORDER, RoleName.ISHCHI.toString(), CommandStatus.GET_ORDER.toString()));
            buttonRepository.save(new Button("Yangi ish yaratish \uD83E\uDDF0", CommandStatus.MAIN_BUTTON, RoleName.BUYURTMACHI.toString(), CommandStatus.CREATE_NEW_JOB.toString()));
            buttonRepository.save(new Button("Buyurtma yaratish \uD83D\uDEE0", CommandStatus.MAIN_BUTTON, RoleName.BUYURTMACHI.toString(), CommandStatus.CREATE_NEW_ORDER.toString()));
            buttonRepository.save(new Button("Yangi foydalanuvchi qo'shish \uD83D\uDD16", CommandStatus.MAIN_BUTTON, "ADMIN", CommandStatus.ADD_NEW_USER.toString()));
            buttonRepository.save(new Button("Hisobot \uD83D\uDCD2", CommandStatus.MAIN_BUTTON, RoleName.ADMIN.toString(), CommandStatus.REPORT.toString()));
            buttonRepository.save(new Button("Kasblar \uD83D\uDC68\u200D\uD83D\uDD27", CommandStatus.MAIN_BUTTON, RoleName.ADMIN.toString(), CommandStatus.GET_PROFESSIONS.toString()));
            buttonRepository.save(new Button("Ish haqi to'g'risida ma'lumot\uD83D\uDCB0", CommandStatus.MAIN_BUTTON, RoleName.ISHCHI.toString(), CommandStatus.SALARY.toString()));
//            buttonRepository.save(new Button("Ishni tahrirlash \uD83D\uDD8C", CommandStatus.MAIN_BUTTON, RoleName.BUYURTMACHI.toString(), CommandStatus.REVIEW_JOB.toString()));
            buttonRepository.save(new Button("Yangi vazifa yaratish \uD83D\uDEE0", CommandStatus.CREATE_NEW_TASK, RoleName.BUYURTMACHI.toString(), CommandStatus.CREATE_NEW_TASK.toString()));
            buttonRepository.save(new Button("Ro'yxatdan o'tish \uD83D\uDCDD", CommandStatus.REGISTRATION, CommandStatus.REGISTRATION.toString()));
            buttonRepository.save(new Button("Bot haqida  ℹ", CommandStatus.REGISTRATION, CommandStatus.ABOUT_BOT.toString()));
            buttonRepository.save(new Button("Orqaga", CommandStatus.BACK, CommandStatus.BACK.toString()));
            buttonRepository.save(new Button("Menu", CommandStatus.MENU, CommandStatus.MENU.toString()));
            buttonRepository.save(new Button("Kontaktni ulashish ☎", CommandStatus.SHARE_CONTACT, CommandStatus.SHARE_CONTACT.toString()));
            buttonRepository.save(new Button("Buyurtmani tasdiqlash ✅", CommandStatus.ORDER_CONFIRMATION, "ADMIN", CommandStatus.ORDER_CONFIRMATION.toString()));
            buttonRepository.save(new Button("set_role_to_user", CommandStatus.SET_ROLE_TO_USER, CommandStatus.SET_ROLE_TO_USER.toString()));
        }
    }
}

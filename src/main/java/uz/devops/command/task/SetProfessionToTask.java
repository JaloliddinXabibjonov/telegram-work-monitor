package uz.devops.command.task;

import static uz.devops.domain.enumeration.Command.CONFIRM_TASK_PROFESSION;
import static uz.devops.domain.enumeration.Command.SET_PROFESSION_TO_TASK;
import static uz.devops.utils.BotUtils.newInlineKeyboardButtonRow;
import static uz.devops.utils.MessageUtils.CONFIRM;
import static uz.devops.utils.MessageUtils.USER_NOT_FOUND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Profession;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.TaskRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.TaskService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@Service(Constants.SET_PROFESSION_TO_TASK)
@RequiredArgsConstructor
public class SetProfessionToTask implements Processor {

    private final MessageSenderService messageSenderService;
    private final ProfessionRepository professionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = update.getCallbackQuery().getData();
        data = data.split("#")[1];
        var userByChatId = userService.findUserByChatId(message.getChatId());
//        if (userByChatId.getSuccess().equals(Boolean.FALSE)) {
//            messageSenderService.sendMessage(message.getChatId(), USER_NOT_FOUND, null);
//            return;
//        }
        var user = userByChatId.getData();
        user.setState(null);
        userRepository.save(user);

        Long taskId = botUtils.getIdForSetRole(message.getText());
        taskService.checkTaskProfession(data    , taskId);

        var markupInline = new InlineKeyboardMarkup();
        var professionMap = new HashMap<Profession, Boolean>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        professionRepository.findAll().forEach(profession -> professionMap.put(profession, professionMap.get(profession) != null));

        taskRepository
            .findById(taskId)
            .ifPresent(task ->
                task.getProfessions().forEach(profession -> professionMap.put(profession, professionMap.get(profession) != null))
            );
        professionMap.forEach((key, value) -> rows.add(BotUtils.newInlineCheckedKeyboardButtonRow(key.getName(), SET_PROFESSION_TO_TASK.getCommandName()+"#"+key.getName(), value)));

        rows.add(newInlineKeyboardButtonRow(CONFIRM, CONFIRM_TASK_PROFESSION.getCommandName()+"#"+taskId));
        markupInline.setKeyboard(rows);

        messageSenderService.sendEditMessage(message.getChatId(), message.getText(), message.getMessageId(), markupInline);
    }
}

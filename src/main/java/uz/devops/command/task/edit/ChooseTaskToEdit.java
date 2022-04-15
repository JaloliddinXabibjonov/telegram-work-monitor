package uz.devops.command.task.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.domain.Job;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.JobRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChooseTaskToEdit implements Processor {

    private final MessageSenderService messageSenderService;
    private final JobRepository jobRepository;
    private final BotUtils botUtils;
    private final UserRepository userRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long parsedJobId = botUtils.getAnyIdFromText(message.getText());

        Optional<Job> optionalJob = jobRepository.findById(parsedJobId);

        if (optionalJob.isEmpty()) {
            log.debug("Job not found with id: {}", parsedJobId);
            return;
        }
        Job job = optionalJob.get();

        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        //        for (Task task : job.getTasks()) {
        //            row.add(newInlineKeyboardButton(String.valueOf(task.getId()), String.valueOf(task.getId())));
        //        }

        rows.add(row);
        markupInline.setKeyboard(rows);

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setState(BotState.EDIT_TASK);
                userRepository.save(user);
            });
        messageSenderService.sendEditMessage(message.getChatId(), message.getText(), message.getMessageId(), markupInline);
    }
}
// TODO:  job tasklarini edit qilyotganda stateni null qilish kerak

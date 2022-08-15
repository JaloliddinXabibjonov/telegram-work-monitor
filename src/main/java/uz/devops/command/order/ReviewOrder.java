package uz.devops.command.order;

import static uz.devops.domain.enumeration.Command.CREATE_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.MessageUtils.NO_WORKS_YET;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Job;
import uz.devops.domain.Task;
import uz.devops.repository.JobRepository;
import uz.devops.service.JobService;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.MessageUtils;

@Service(Constants.CREATE_NEW_ORDER)
@RequiredArgsConstructor
public class ReviewOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final JobRepository jobRepository;
    private final MessageUtils messageUtils;
    private final JobService jobService;

    @Override
    public void execute(Update update) {
        var markupInline = newInlineKeyboardMarkup(
            List.of(newInlineKeyboardButton(MessageUtils.CREATE_ORDER, CREATE_ORDER.getCommandName()))
        );

        var availableJobs = jobService.findAllJobs();

        if (availableJobs.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(update.getMessage().getChatId(), NO_WORKS_YET, null);
            return;
        }

        for (Job job : availableJobs.getData()) {
            List<Task> tasksByJobId = jobRepository.findTasksByJobId(job.getId());
            messageSenderService.sendMessage(update.getMessage().getChatId(), messageUtils.getJobs(job, tasksByJobId), markupInline);
        }
    }
}

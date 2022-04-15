package uz.devops.command.job;

import static uz.devops.domain.enumeration.Command.ADD_TASK_TO_JOB;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.MessageUtils.CREATE_TASK;
import static uz.devops.utils.MessageUtils.NO_WORKS_YET;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Job;
import uz.devops.repository.JobRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.MessageUtils;

@Service
@RequiredArgsConstructor
public class ReviewJob implements Processor {

    private final MessageSenderService messageSenderService;
    private final JobRepository jobRepository;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        var markupInline = newInlineKeyboardMarkup(
            List.of(
                //                newInlineKeyboardButton(EDIT_ICON, CHOOSE_ONE_TO_EDIT.getCommandName()),
                newInlineKeyboardButton(CREATE_TASK, ADD_TASK_TO_JOB.getCommandName())
                //                newInlineKeyboardButton(REMOVE_BASKET_ICON, REMOVE_JOB.getCommandName())
            )
        );

        if (jobRepository.findAll().isEmpty()) {
            messageSenderService.sendMessage(update.getMessage().getChatId(), NO_WORKS_YET, null);
            return;
        }

        for (Job job : jobRepository.findAll()) {
            var tasksByJobId = jobRepository.findTasksByJobId(job.getId());
            messageSenderService.sendMessage(update.getMessage().getChatId(), messageUtils.getJobs(job, tasksByJobId), markupInline);
        }
    }
}

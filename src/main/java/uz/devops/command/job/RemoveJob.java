package uz.devops.command.job;

import static uz.devops.utils.MessageUtils.JOB_REMOVED;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.JobRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@Service
@RequiredArgsConstructor
public class RemoveJob implements Processor {

    private final MessageSenderService messageSenderService;
    private final JobRepository jobRepository;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long parsedJobId = botUtils.getAnyIdFromText(message.getText());

        jobRepository
            .findById(parsedJobId)
            .ifPresent(job -> {
                jobRepository.deleteById(parsedJobId);
                messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
                messageSenderService.sendMessage(message.getChatId(), JOB_REMOVED, null);
            });
    }
}

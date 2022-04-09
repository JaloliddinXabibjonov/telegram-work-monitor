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
import uz.devops.repository.JobRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service
public class CreateOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final JobRepository jobRepository;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        var markupInline = newInlineKeyboardMarkup(List.of(newInlineKeyboardButton("Order  ➕", CREATE_ORDER.getCommandName())));

        if (jobRepository.findAll().isEmpty()) {
            messageSenderService.sendMessage(update.getMessage().getChatId(), NO_WORKS_YET, null);
            return;
        }
        jobRepository
            .findAll()
            .forEach(job -> messageSenderService.sendMessage(update.getMessage().getChatId(), messageUtils.getJobs(job), markupInline));
    }
}

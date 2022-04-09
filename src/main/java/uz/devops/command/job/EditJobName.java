package uz.devops.command.job;

import static uz.devops.utils.MessageUtils.JOB_NAME_EDITED;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.JobRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class EditJobName implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final BotUtils botUtils;
    private final UserService userService;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                if (user.getState() == BotState.ENTER_JOB_NEW_NAME) {
                    jobRepository
                        .findById(user.getTempTableId())
                        .ifPresent(job -> {
                            job.setName(message.getText());
                            jobRepository.save(job);
                            user.setState(null);
                            userRepository.save(user);
                            messageSenderService.sendMessage(
                                update.getMessage().getChatId(),
                                JOB_NAME_EDITED,
                                botUtils.getMainReplyKeyboard(String.valueOf(message.getChatId()))
                            );
                        });
                }
            });
    }
}

package uz.devops.command.job;

import static uz.devops.utils.MessageUtils.JOB_NAME_EDITED;
import static uz.devops.utils.MessageUtils.USER_NOT_FOUND;

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

@Service
@RequiredArgsConstructor
public class EditJobName implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final UserService userService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();

        var userByChatId = userService.findUserByChatId(message.getChatId());
        if (userByChatId.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(message.getChatId(), USER_NOT_FOUND, null);
            return;
        }

        var user = userByChatId.getData();
        if (user.getState().equals(BotState.ENTER_JOB_NEW_NAME)) {
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
    }
}

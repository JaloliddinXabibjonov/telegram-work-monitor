package uz.devops.command.job;

import static uz.devops.domain.enumeration.Command.ENTER_JOB_NAME;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Job;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.UserRepository;
import uz.devops.service.JobService;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Service
public class CreateJob implements Processor {

    private final MessageSenderService messageSenderService;
    private final BotUtils botUtils;
    private final UserRepository userRepository;
    private final JobService jobService;
    private final UserService userService;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();

        userService
            .findByChatId(chatId)
            .ifPresent(user -> {
                if (user.getState() == BotState.ENTER_JOB_NAME) {
                    user.setState(BotState.ENTER_TASK_NAME);
                    Job job = jobService.createNewJob(update.getMessage());
                    user.setTempTableId(job.getId());
                    userRepository.save(user);

                    messageSenderService.sendMessage(chatId, messageUtils.getJobInfo(job), null);
                } else {
                    user.setState(BotState.ENTER_JOB_NAME);
                    userRepository.save(user);
                    messageSenderService.sendMessage(chatId, ENTER_JOB_NAME.getCommandName(), botUtils.keyboardRemove(true));
                }
            });
    }
}

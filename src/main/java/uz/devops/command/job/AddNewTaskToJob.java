package uz.devops.command.job;

import static uz.devops.domain.enumeration.Command.ENTER_TASK_NAME;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@Service
@RequiredArgsConstructor
public class AddNewTaskToJob implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long jobId = botUtils.getAnyIdFromText(message.getText());

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                user.setState(BotState.ENTER_TASK_NAME);
                user.setTempTableId(jobId);
                userRepository.save(user);
                messageSenderService.sendMessage(message.getChatId(), ENTER_TASK_NAME.getCommandName(), null);
                messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
            });
    }
}

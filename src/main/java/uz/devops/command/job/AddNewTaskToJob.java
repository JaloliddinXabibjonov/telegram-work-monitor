package uz.devops.command.job;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class AddNewTaskToJob implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long jobId = botUtils.getAnyIdFromText(message.getText());

        userService
            .findByChatId(message.getChatId())
            .ifPresent(user -> {
                user.setState(BotState.ENTER_TASK_NAME);
                user.setTempTableId(jobId);
                userRepository.save(user);
                messageSenderService.sendMessage(message.getChatId(), "Vazifa nomini kiriting", null);
                messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
            });
    }
}

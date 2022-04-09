package uz.devops.command.order;

import static uz.devops.domain.enumeration.BotState.ENTER_ORDER_DESCRIPTION;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Task;
import uz.devops.repository.UserRepository;
import uz.devops.service.JobService;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

@RequiredArgsConstructor
@Service
public class EnterOrderFields implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final JobService jobService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long jobId = botUtils.getAnyIdFromText(message.getText());

        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                Task availableTask = jobService.getAvailableTask(jobId);

                user.setState(ENTER_ORDER_DESCRIPTION);
                user.setTempTableId(jobId);
                user.setExtraTableId(availableTask.getId());
                userRepository.save(user);

                messageSenderService.deleteMessage(message.getMessageId(), String.valueOf(message.getChatId()));
                messageSenderService.sendMessage(
                    message.getChatId(),
                    "Task  #" + availableTask.getId() + "\n" + "\n" + availableTask.getName() + "\n" + "Buyurtma haqida ma'lumot kiriting",
                    botUtils.keyboardRemove(true)
                );
            });
    }
}

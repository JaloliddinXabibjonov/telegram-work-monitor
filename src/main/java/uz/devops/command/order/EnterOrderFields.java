package uz.devops.command.order;

import static uz.devops.utils.MessageUtils.ENTER_DESC_ORDER;
import static uz.devops.utils.MessageUtils.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.enumeration.BotState;
import uz.devops.repository.UserRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;

@Service
@RequiredArgsConstructor
public class EnterOrderFields implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        var callbackMessage = update.getCallbackQuery().getMessage();
        var jobId = botUtils.getAnyIdFromText(callbackMessage.getText());
        var userByChatId = userService.findUserByChatId(callbackMessage.getChatId());

        if (userByChatId.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(callbackMessage.getChatId(), USER_NOT_FOUND, null);
            return;
        }

        var user = userByChatId.getData();
        user.setTempTableId(jobId);
        user.setState(BotState.ENTER_ORDER_DESCRIPTION);
        userRepository.save(user);

        messageSenderService.deleteMessage(callbackMessage.getMessageId(), String.valueOf(callbackMessage.getChatId()));
        messageSenderService.sendMessage(callbackMessage.getChatId(), ENTER_DESC_ORDER, null);
    }
}

package uz.devops.command.user;

import static uz.devops.utils.MessageUtils.ROLE_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.repository.ProfessionRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

@RequiredArgsConstructor
@Slf4j
@Service
public class AssignRoleToUser implements Processor {

    private final MessageSenderService messageSenderService;
    private final ProfessionRepository professionRepository;
    private final BotUtils botUtils;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (professionRepository.findAll().isEmpty()) {
            log.debug("Professions not found");
            messageSenderService.sendMessage(chatId, ROLE_NOT_FOUND, null);
            return;
        }
        Message message = update.getCallbackQuery().getMessage();
        Long parsedUserId = botUtils.getUserIdFromText(message.getText());

        var professionsKeyboard = BotUtils.getProfessionsKeyboard(professionRepository);
        messageSenderService.sendEditMessage(
            chatId,
            messageUtils.assignRoleToUser(parsedUserId),
            message.getMessageId(),
            professionsKeyboard
        );
    }
}

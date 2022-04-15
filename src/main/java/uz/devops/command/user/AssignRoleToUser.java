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

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignRoleToUser implements Processor {

    private final MessageSenderService messageSenderService;
    private final ProfessionRepository professionRepository;
    private final BotUtils botUtils;
    private final MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long parsedUserId = botUtils.getUserIdFromText(message.getText());

        if (professionRepository.findAll().isEmpty()) {
            log.debug("Professions not found");
            messageSenderService.sendMessage(message.getChatId(), ROLE_NOT_FOUND, null);
            return;
        }

        var professionsKeyboard = BotUtils.getProfessionsKeyboard(professionRepository);
        messageSenderService.sendEditMessage(
            message.getChatId(),
            messageUtils.assignRoleToUser(parsedUserId),
            message.getMessageId(),
            professionsKeyboard
        );
    }
}

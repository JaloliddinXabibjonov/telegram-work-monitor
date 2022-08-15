package uz.devops.command.other;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.UserProfilePhotos;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.OrderTask;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import static uz.devops.domain.enumeration.Command.SHARE_CONTACT;
import static uz.devops.utils.BotUtils.newKeyboardButton;
import static uz.devops.utils.MessageUtils.SHARE_CONTACT_FOR_REGISTRATION;
import static uz.devops.utils.MessageUtils.YOUR_SALARY_OF_THIS_MONTH;

@RequiredArgsConstructor
@Service(Constants.SALARY)
public class SalaryCommand implements Processor {

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private OrderTaskRepository orderTaskRepository;


    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long salary=0L;
        Instant startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay().plusMinutes(1440).toInstant(ZoneOffset.UTC);
        List<OrderTask> orderTaskList = orderTaskRepository.findAllByStatusAndEmployeeUsernameAndEndDateBetween(Status.DONE, update.getMessage().getChat().getUserName(), startOfMonth, endOfMonth);
        if (orderTaskList.isEmpty()){
            messageSenderService.sendMessage(chatId, MessageUtils.NOT_FOUND, null);
            return;
        }
        for (OrderTask orderTask : orderTaskList) {
            salary+=orderTask.getTask().getPrice();
        }

        messageSenderService.sendMessage(update.getMessage().getChatId(), YOUR_SALARY_OF_THIS_MONTH+salary, null);
    }
}

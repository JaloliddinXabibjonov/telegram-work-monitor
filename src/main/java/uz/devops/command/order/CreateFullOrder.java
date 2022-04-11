package uz.devops.command.order;

import static uz.devops.domain.enumeration.Command.CONFIRM_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.MessageUtils.CONFIRM;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.Order;
import uz.devops.domain.Task;
import uz.devops.domain.TaskInfo;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.security.AuthoritiesConstants;
import uz.devops.service.JobService;
import uz.devops.service.MessageSenderService;
import uz.devops.service.TaskService;
import uz.devops.utils.BotUtils;

@Service
@RequiredArgsConstructor
public class CreateFullOrder implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderRepository orderRepository;
    private final JobService jobService;
    private final TaskService taskService;
    private final BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long jobId = botUtils.getAnyIdFromText(message.getText());

        SimpleResultData<Task> response = jobService.getAvailableTask(jobId);
        if (Objects.equals(Boolean.FALSE, response.getSuccess())) {
            messageSenderService.sendMessage(message.getChatId(), "Bu ish bo'yicha vazifalar tugatilgan", null);
            return;
        }

        Task availableTask = response.getData();

        Order order = new Order();
        order.setStatus(Status.TO_DO);
        order.setCreatedDate(Instant.now());
        order.setCreatedBy(AuthoritiesConstants.ADMIN);
        orderRepository.save(order);

        jobService.addOrderToJob(jobId, order);

        TaskInfo taskInfo = taskService.findTaskInfoByTaskId(availableTask.getId());

        var confirmKeyboard = newInlineKeyboardMarkup(List.of(newInlineKeyboardButton(CONFIRM, CONFIRM_ORDER.getCommandName())));

        messageSenderService.sendMessage(
            message.getChatId(),
            "Vazifa  #" +
            availableTask.getId() +
            "\n" +
            "Buyurtma  #" +
            order.getId() +
            "\n" +
            "Info  #" +
            taskInfo.getId() +
            "\n" +
            "\n" +
            "\uD83D\uDCB0  " +
            taskInfo.getPrice() +
            "\n" +
            "\uD83D\uDCDC  " +
            taskInfo.getDescription(),
            confirmKeyboard
        );
    }
}

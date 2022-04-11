package uz.devops.command.order;

import static uz.devops.domain.enumeration.BotState.ENTER_ORDER_DESCRIPTION;
import static uz.devops.domain.enumeration.Command.CONFIRM_ORDER;
import static uz.devops.utils.BotUtils.newInlineKeyboardButton;
import static uz.devops.utils.BotUtils.newInlineKeyboardMarkup;
import static uz.devops.utils.MessageUtils.CONFIRM;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.domain.Order;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.JobService;
import uz.devops.service.MessageSenderService;

@RequiredArgsConstructor
@Service
public class CreateOrderViaState implements Processor {

    private final MessageSenderService messageSenderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final JobService jobService;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();
        userRepository
            .findByChatId(String.valueOf(message.getChatId()))
            .ifPresent(user -> {
                if (user.getState() != null && user.getState().equals(ENTER_ORDER_DESCRIPTION)) {
                    Order order = new Order();
                    order.setDescription(message.getText());
                    order.setStatus(Status.TO_DO);
                    orderRepository.save(order);

                    jobService.addOrderToJob(user.getTempTableId(), order);

                    user.setState(null);
                    user.setTempTableId(order.getId());
                    userRepository.save(user);

                    var confirmKeyboard = newInlineKeyboardMarkup(
                        List.of(newInlineKeyboardButton(CONFIRM, CONFIRM_ORDER.getCommandName()))
                    );

                    messageSenderService.sendMessage(
                        message.getChatId(),
                        "Task  #" +
                        user.getExtraTableId() +
                        "\n" +
                        "Buyurtma  #" +
                        order.getId() +
                        "\n" +
                        "\n" +
                        "\uD83D\uDCDC  " +
                        order.getDescription(),
                        confirmKeyboard
                    );
                }
            });
    }
}

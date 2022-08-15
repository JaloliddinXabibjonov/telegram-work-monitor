package uz.devops.command.order;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Order;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.OrderTaskService;
import uz.devops.utils.BotUtils;

import java.util.Optional;

import static uz.devops.utils.MessageUtils.SERVER_ERROR;

@Service(Constants.REJECT_ORDER)
@RequiredArgsConstructor
public class RejectOrder implements Processor {

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private OrderTaskService orderTaskService;
    @Autowired
    private BotUtils botUtils;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = update.getCallbackQuery().getData();
        Long orderId = Long.valueOf(data.substring(data.indexOf("#") + 1));
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            order.setStatus(Status.DELETED);
            orderRepository.save(order);
        }
        messageSenderService.deleteMessage(
            message.getMessageId(), message.getChatId().toString());
        messageSenderService.sendMessage(message.getChatId(),
            "Buyurma bekor qilindi!",
            botUtils.createMainButtonsByRole(message.getChatId())
        );
    }
}

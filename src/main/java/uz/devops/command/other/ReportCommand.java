package uz.devops.command.other;

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
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@RequiredArgsConstructor
@Service(Constants.REPORT)
public class ReportCommand implements Processor {

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private BotUtils botUtils;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MessageUtils messageUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        List<Order> orderList = orderRepository.findAllByStatusAndEndDateBetween(Status.DONE, LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC),
            LocalDate.now().atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC));
        if (orderList.isEmpty()){
            messageSenderService.sendMessage(chatId, "Buyurtma topilmadi!",null);
            return;
        }
        for (Order order : orderList) {
            messageSenderService.sendMessage(chatId, messageUtils.getOrderInfo(order), null);
        }
    }
}

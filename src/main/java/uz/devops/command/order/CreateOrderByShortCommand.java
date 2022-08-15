package uz.devops.command.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.config.Constants;
import uz.devops.domain.Job;
import uz.devops.domain.Order;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.Command;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.JobRepository;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.UserRepository;
import uz.devops.service.*;
import uz.devops.utils.BotUtils;
import uz.devops.utils.MessageUtils;
import java.util.List;
import java.util.Optional;

import static uz.devops.domain.enumeration.Command.ADD_ORDER_BY_SHORT_COMMAND;
import static uz.devops.utils.MessageUtils.*;
@Slf4j
@Service(Constants.ADD_ORDER_BY_SHORT_COMMAND)
@RequiredArgsConstructor
public class CreateOrderByShortCommand implements Processor {

    @Autowired
    private  MessageSenderService messageSenderService;
    @Autowired
    private  OrderRepository orderRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  MessageUtils messageUtils;
    @Autowired
    private  BotUtils botUtils;
    @Autowired
    private JobRepository jobRepository;

    @Override
    public void execute(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Optional<User> optionalUser1 = userRepository.findByChatId(chatId.toString());
        if (optionalUser1.isEmpty()){
            messageSenderService.sendMessage(chatId, "Siz ro'yxatdan o'tmagansiz!", null);
            return;
        }
        Boolean adminOrCustomer = botUtils.checkUserForAdminOrCustomer(optionalUser1.get());
        if (!adminOrCustomer){
            messageSenderService.sendMessage(chatId, UNKNOWN_MESSAGE, null);
            return;
        }
        String text = message.getText();
        String data = text.substring(ADD_ORDER_BY_SHORT_COMMAND.getCommandName().length()+1);
        String[] split = data.split("/");
        log.info("Order  {}", data);
        String messageText;
        if (split.length>=3){
            String count=split[0];
            String jobId=split[1];
            String customerPhoneNumber=split[2];
            String description=split.length==3?"":split[3];
            log.info("Description info {}","/"+description+"/");
            try {
                int parsedCount=Integer.parseInt(count);
                Long parsedJobId=Long.parseLong(jobId);
                Optional<Job> optionalJob = jobRepository.findById(parsedJobId);
                if (optionalJob.isPresent()){
                    Job job = optionalJob.get();
                    Optional<User> optionalUser = userRepository.findByPhoneNumber(customerPhoneNumber);
                    if (optionalUser.isPresent()){
                        User user = optionalUser.get();
                        Order order=new Order();
                        order.setCount(parsedCount);
                        order.setStatus(Status.NEW);
                        order.setDescription(description.trim().equals("")?null:description.trim());
                        order.setJob(job);
                        order.setCustomer(user);
                        Order save = orderRepository.save(order);
                        messageText=messageUtils.getOrderInfo(save);
                        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rows=List.of(botUtils.createInlineKeyboardRow(MessageUtils.CONFIRM, Command.CONFIRM_ORDER.getCommandName()+"#"+order.getId()),botUtils.createInlineKeyboardRow(MessageUtils.REJECT_ORDER, Command.REJECT_ORDER.getCommandName()+"#"+save.getId()) );
                        inlineKeyboardMarkup.setKeyboard(rows);
                            messageSenderService.sendMessage(chatId, messageText, inlineKeyboardMarkup);

                    }else {
                        messageText=customerPhoneNumber+" telefon raqamli mijoz topilmadi!";
                        messageSenderService.sendMessage(chatId, messageText, botUtils.createMainButtonsByRole(message.getChatId()));
                    }
                }else {
                    messageText=parsedJobId+" raqamli ish topilmadi!";
                    messageSenderService.sendMessage(chatId, messageText, botUtils.createMainButtonsByRole(message.getChatId()));
                }
            }catch (Exception e){
                messageText= CLIENT_ERROR;
                messageSenderService.sendMessage(chatId, messageText, botUtils.createMainButtonsByRole(message.getChatId()));
            }
        }else {
            messageSenderService.sendMessage(chatId, "Buyurtma berishda xatolik!", botUtils.createMainButtonsByRole(message.getChatId()));
        }
    }
}

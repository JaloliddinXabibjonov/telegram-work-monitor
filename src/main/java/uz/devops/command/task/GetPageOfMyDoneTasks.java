package uz.devops.command.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.command.Processor;
import uz.devops.command.ResultData;
import uz.devops.config.Constants;
import uz.devops.domain.OrderTask;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.utils.BotUtils;

import static uz.devops.utils.MessageUtils.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service(Constants.GET_PAGE_OF_MY_DONE_TASKS)
public class GetPageOfMyDoneTasks implements Processor {
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private OrderTaskRepository orderTaskRepository;
    @Autowired
    private BotUtils botUtils;

    @Override
    public void execute(Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String employeeUserName = message.getChat().getUserName();
        int page = Integer.parseInt(update.getCallbackQuery().getData().split("#")[1]);
        if (page < 0) {
            messageSenderService.answerCallbackQuery(update.getCallbackQuery().getId(), NOT_FOUND);
            return;
        }
        Pageable pageable = PageRequest.of(page, BotUtils.SIZE);
        Page<OrderTask> orderTasks = orderTaskRepository.findAllByEmployeeUsernameAndStatusOrderByEndDateDesc(employeeUserName, Status.DONE, pageable);
        if (orderTasks.isEmpty()) {
            messageSenderService.answerCallbackQuery(update.getCallbackQuery().getId(), NOT_FOUND);
            return;
        }
        ResultData data = botUtils.createMyDoneTasksByPageable(orderTasks.toList(), page);
        messageSenderService.sendEditMessage(chatId, data.getMessage(), message.getMessageId(), data.getInlineKeyboardMarkup());
    }

}

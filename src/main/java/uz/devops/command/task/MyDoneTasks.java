package uz.devops.command.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.command.Processor;
import uz.devops.command.ResultData;
import uz.devops.config.Constants;
import uz.devops.domain.OrderTask;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.MessageSenderService;
import uz.devops.service.TaskService;
import uz.devops.service.UserService;
import uz.devops.utils.BotUtils;
import uz.devops.utils.Icons;
import uz.devops.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import static uz.devops.domain.enumeration.Command.GET_ORDER_TASK_BY_ID;
import static uz.devops.domain.enumeration.Command.GET_PAGE_OF_MY_DONE_TASKS;
import static uz.devops.utils.MessageUtils.USER_NOT_FOUND;
import static uz.devops.utils.MessageUtils.USER_WORK_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service(Constants.MY_DONE_TASKS)
public class MyDoneTasks implements Processor {

    private final MessageSenderService messageSenderService;
    private final OrderTaskRepository orderTaskRepository;
    @Autowired
    private BotUtils botUtils;
    private final UserService userService;

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        var resultData = userService.findUserByChatId(chatId);
        if (resultData.getSuccess().equals(Boolean.FALSE)) {
            messageSenderService.sendMessage(chatId, USER_NOT_FOUND, null);
            return;
        }

        int page = BotUtils.PAGE;
        int size = BotUtils.SIZE;
        Pageable pageable = Pageable.ofSize(size);
        Page<OrderTask> orderTasks = orderTaskRepository.findAllByEmployeeUsernameAndStatusOrderByEndDateDesc(resultData.getData().getTgUsername(), Status.DONE, pageable);
        if (orderTasks.isEmpty()) {
            messageSenderService.sendMessage(chatId, USER_WORK_NOT_FOUND, null);
            return;
        }
        ResultData data = botUtils.createMyDoneTasksByPageable(orderTasks.toList(), page);
        messageSenderService.sendMessage(chatId, data.getMessage(), data.getInlineKeyboardMarkup());
    }



}

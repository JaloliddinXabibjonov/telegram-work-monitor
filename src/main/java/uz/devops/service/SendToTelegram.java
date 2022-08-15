package uz.devops.service;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import uz.devops.service.dto.ResultTelegram;

public interface SendToTelegram {
    ResultTelegram sendSendMessage(String url, SendMessage sendMessage);
    ResultTelegram sendEditMessageText(String url, EditMessageText editMessageText);
    Boolean sendDeleteMessage(String url, DeleteMessage deleteMessage);
}

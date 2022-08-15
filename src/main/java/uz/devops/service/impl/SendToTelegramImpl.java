package uz.devops.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import uz.devops.service.SendToTelegram;
import uz.devops.service.dto.ResultTelegram;

@Service
public class SendToTelegramImpl implements SendToTelegram {

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public ResultTelegram sendSendMessage(String url, SendMessage sendMessage) {
        return restTemplate.postForObject(url, sendMessage, ResultTelegram.class);
    }

    @Override
    public ResultTelegram sendEditMessageText(String url, EditMessageText editMessageText) {
        return restTemplate.postForObject(url, editMessageText, ResultTelegram.class);
    }

    @Override
    public Boolean sendDeleteMessage(String url, DeleteMessage deleteMessage) {
        return restTemplate.postForObject(url, deleteMessage, Boolean.class);

    }
}

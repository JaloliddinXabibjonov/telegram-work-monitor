package uz.devops.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.devops.WorkMonitorBot;
import uz.devops.config.Constants;
import uz.devops.service.MessageSenderService;
import uz.devops.service.SendToTelegram;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageSenderServiceImpl implements MessageSenderService {

    @Autowired
    private SendToTelegram sendToTelegram;
    @Autowired
    private WorkMonitorBot workMonitorBot;

    @Override
    public void sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setParseMode(ParseMode.HTML);
//        sendToTelegram.sendSendMessage(Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage", sendMessage);
        try {
            workMonitorBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void sendEditMessage(Long chatId, String message, Integer messageId, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setMessageId(messageId);
        editMessageText.setText(message);
        editMessageText.setParseMode(ParseMode.HTML);
        editMessageText.setReplyMarkup(keyboardMarkup);
//        sendToTelegram.sendEditMessageText(Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/editMessageText", editMessageText);
        try {
            workMonitorBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageForAdmin(List<String> chatIdList, String message, ReplyKeyboard replyKeyboard) {
        for (String chatId : chatIdList) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sendMessage.setReplyMarkup(replyKeyboard);
            sendMessage.setParseMode(ParseMode.HTML);
//            sendToTelegram.sendSendMessage(Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage", sendMessage);
            try {
                workMonitorBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void deleteMessage(Integer messageId, String chatId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(chatId);
//        sendToTelegram.sendDeleteMessage(Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage", deleteMessage);
        try {
            workMonitorBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void answerCallbackQuery(String callBackQueryId, String text) {
        AnswerCallbackQuery answerCallbackQuery=new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callBackQueryId);
        answerCallbackQuery.setText(text);
        answerCallbackQuery.setCacheTime(3);
        try {
            workMonitorBot.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessageWithReply(String chatId, String message, Integer replyMessageId) {
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyToMessageId(replyMessageId);
        try {
            workMonitorBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}

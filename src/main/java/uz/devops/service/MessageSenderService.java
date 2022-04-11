package uz.devops.service;

import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface MessageSenderService {
    void sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboard);

    void sendEditMessage(Long chatId, String message, Integer messageId, InlineKeyboardMarkup keyboardMarkup);

    void sendMessageForAdmin(List<String> chatIdList, String message, ReplyKeyboard replyKeyboard);

    void deleteMessage(Integer messageId, String chatId);
}

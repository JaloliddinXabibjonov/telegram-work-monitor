package uz.devops.command;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Setter
public class ResultData {

    private String message;
    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public ResultData(String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        this.message = message;
        this.inlineKeyboardMarkup = inlineKeyboardMarkup;
    }
}

package uz.devops.service.dto;

import org.telegram.telegrambots.meta.api.objects.Message;

public class ResultTelegram {
    private boolean ok;
    private Message message;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}

package uz.devops.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Processor {
    void execute(Update update);
}

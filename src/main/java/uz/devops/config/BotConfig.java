package uz.devops.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotConfig {

    private String name;

    private String token;

    private String adminChatId;

}

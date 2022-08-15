package uz.devops.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uz.devops.service.dto.ResultTelegram;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
public class BotConfig {

    private String name;

    private String token;

    private String adminChatId;

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}

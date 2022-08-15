package uz.devops.command.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Authority;
import uz.devops.domain.User;
import uz.devops.domain.enumeration.RoleName;
import uz.devops.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class GetRoleFromUser {

    @Autowired
    private UserRepository userRepository;

    public Set<Authority> getFromMessage(Update update){
        Long chatId = update.getMessage().getChatId();
        Optional<User> optionalUser = userRepository.findByChatId(chatId.toString());
        return optionalUser.isPresent()?optionalUser.get().getAuthorities():new HashSet<>();
    }

    public Set<Authority> getFromCallBackQuery(Update update){
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Optional<User> optionalUser = userRepository.findByChatId(chatId.toString());
        return optionalUser.isPresent()?optionalUser.get().getAuthorities():new HashSet<>();
    }

    public Boolean checkUserFromMessage(User user){
        Set<Authority> authorities = user.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getName().equals(RoleName.ISHCHI.toString()));
    }

    public Boolean checkUser(User user){
        Set<Authority> authorities = user.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getName().equals(RoleName.ISHCHI.toString()));
    }



    public Boolean checkAdminFromMessage(User user){
        Set<Authority> authorities = user.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getName().equals("ADMIN"));
    }

    public Boolean checkClientFromMessage(User user){
        Set<Authority> authorities = user.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getName().equals(RoleName.BUYURTMACHI.toString()));
    }

    public Boolean checkUserFromCallBackQuery(User user){
        Set<Authority> authorities = user.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getName().equals(RoleName.ISHCHI.toString()));
    }

    public Boolean checkAdminFromCallBackQuery(User user){
        Set<Authority> authorities = user.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getName().equals("ADMIN"));
    }

    public Boolean checkClientFromCallBackQuery(User user){
        Set<Authority> authorities = user.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getName().equals(RoleName.BUYURTMACHI.toString()));
    }

}

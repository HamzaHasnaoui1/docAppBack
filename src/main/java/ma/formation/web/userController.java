package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.security.repositories.AppUserRepository;
import ma.formation.security.service.UserDetailsServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class userController {
    AppUserRepository appUserRepository;
    UserDetailsServiceImpl userDetailsService;


    @GetMapping(path = "/user/account")
    public String profile() {
        return "account";
    }


}

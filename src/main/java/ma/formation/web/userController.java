package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.security.entities.AppRole;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppUserRepository;
import ma.formation.security.service.UserDetailsServiceImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
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

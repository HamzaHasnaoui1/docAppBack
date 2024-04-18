package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.security.entities.AppRole;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;
@Controller
@AllArgsConstructor
public class userController {
    AppUserRepository appUserRepository;
    @GetMapping("/user/monprofile")
    public String profile(Model model, String username){
        AppUser appUser = appUserRepository.findByUsername(username);
        List<AppRole> roles = new ArrayList<>();
        for (AppRole role: appUser.getAppRoles()) {
            roles.add(role);
        }
        model.addAttribute("listroles",roles);
        model.addAttribute("user",appUser);
        return "profile";
    }
}

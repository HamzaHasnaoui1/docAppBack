package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppUserRepository;
import ma.formation.security.service.SecurityService;
import ma.formation.security.service.UserDetailsServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    AppUserRepository appUserRepository;
    UserDetailsServiceImpl userDetailsService;
    SecurityService securityService;


    @GetMapping(path = "/user/account")
    public String profile() {
        return "account";
    }

    @GetMapping("/admin/as")//lien principal
    public String adminPage(Model model) {
        List<AppUser> users = appUserRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", List.of("USER", "ADMIN"));
        return "admin";
    }

    @PostMapping("/admin/users")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String  numeroTelephone,
                             @RequestParam String password,
                             @RequestParam String verifyPassword,
                             Model model) {
        try {
            AppUser newUser = securityService.saveNewUser(username,email,numeroTelephone, password, verifyPassword);
            model.addAttribute("message", "Utilisateur créé avec succès : " + newUser.getUsername());
            return "redirect:/admin/as";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "admin";
    }

    @PostMapping("/admin/users/addRole")
    public String assignRoleToUser(@RequestParam String username,
                                   @RequestParam String roleName,
                                   Model model) {
        try {
            securityService.addRoleToUser(username, roleName);
            model.addAttribute("message", "Rôle " + roleName + " affecté à l'utilisateur : " + username);
            return "redirect:/admin/as";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Erreur : " + e.getMessage());
        }
        List<AppUser> users = appUserRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", List.of("USER", "ADMIN"));
        return "admin";
    }

    @PostMapping("/admin/users/removeRole")
    public String removeRoleFromUser(@RequestParam String username,
                                     @RequestParam String roleName,
                                     Model model) {
        try {
            securityService.removeRoleFromUser(username, roleName);
            model.addAttribute("message", "Rôle " + roleName + " retiré de l'utilisateur : " + username);
            return "redirect:/admin/as";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Erreur : " + e.getMessage());
        }
        updateModel(model);
        return "admin";
    }

    private void updateModel(Model model) {
        List<AppUser> users = appUserRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", List.of("USER", "ADMIN"));
    }
}

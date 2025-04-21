package ma.formation.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;

@Controller
public class SecurityController {

    private static final String FRONT_URL = "http://localhost:8089/";

    @GetMapping("/403")
    public String notAuthorized() {
        return "redirect:" + FRONT_URL + "/error/403";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes attributes)
            throws ServletException {
        request.logout();
        attributes.addFlashAttribute("logoutMessage", "Déconnexion réussie");
        return "redirect:" + FRONT_URL + "/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:" + FRONT_URL + "/login";
    }

    @GetMapping("/reset_password")
    public String resetPassword() {
        return "redirect:" + FRONT_URL + "/reset-password";
    }

    @GetMapping("/about_us")
    public String aboutUs() {
        return "redirect:" + FRONT_URL + "/about";
    }

    @GetMapping("/contact_us")
    public String contactUs() {
        return "redirect:" + FRONT_URL + "/contact";
    }

}
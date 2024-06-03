package ma.formation.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SecurityController {
    @GetMapping("/403")
    public String notAuthorized(){
        return "403";
    }

    @GetMapping("/login")
    public String login(){ return "login";}



    @GetMapping(path="/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }

    /*@GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attributes) {
        // Perform logout actions (invalidate session, clear cookies, etc.)
        request.getSession().invalidate();
        attributes.addFlashAttribute("logoutMessage", "You have been logged out successfully.");
        return "redirect:/"; // Redirect to the home page
    }*/

    @GetMapping(path="/login?logout")
    public String logout1(){
        return "/home";
    }

    @GetMapping(path="/")
    public String home(){
        return "home";
    }

    @GetMapping(path="/home")
    public String home1(){
        return "home";
    }

    @GetMapping(path="/homeold")
    public String homeold(){
        return "homeold";
    }

    @GetMapping(path="/reset_password")
    public String reset_password(){
        return "reset_password";
    }


    @GetMapping(path="/about_us")
    public String about_us(){
        return "about_us";
    }

    @GetMapping(path="/contact_us")
    public String contact_us(){
        return "contact_us";
    }

    @GetMapping(path="/user/index")
    public String index(){
        return "index";
    }

}

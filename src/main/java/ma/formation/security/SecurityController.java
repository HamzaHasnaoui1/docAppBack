package ma.formation.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class SecurityController {
    @GetMapping("/403")
    public String notAuthorized(){
        return "403";
    }

    @GetMapping("/login")
    public String login(){ return "login";}

    @GetMapping(path="/logout")
    public String logout(){
        return "redirect:/home";
    }

    /*@GetMapping(path="/login?logout")
    public String logout1(){
        return "redirect:/home";
    }*/


}

package ma.formation.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditTemplate {

    @GetMapping("/admin/editTemplate")
    public String editCarouselContent(Model model) {
        // Add any necessary data to the model
        return "editTemp";
    }

    @PostMapping("/admin/saveTemplate")
    public String saveCarouselContent(@RequestParam("content") String content) {
        // Save the content to your data store
        // This is just a placeholder, implement your save logic here

        System.out.println("Content saved: " + content);

        return "redirect:/admin/editCarouselContent";
    }
}
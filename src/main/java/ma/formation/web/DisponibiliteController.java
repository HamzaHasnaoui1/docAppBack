package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.DisponibiliteMedecin;
import ma.formation.repositories.DisponibiliteMedecinRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class DisponibiliteController {
    private final DisponibiliteMedecinRepository disponibiliteMedecinRepository;

    @PostMapping("/admin/dispoMedecin")
    public DisponibiliteMedecin create(@RequestBody DisponibiliteMedecin d) {
        return disponibiliteMedecinRepository.save(d); }

    @GetMapping("/user/dispoMedecin/{id}")
    public List<DisponibiliteMedecin> byMedecin(@PathVariable Long id) {
        return disponibiliteMedecinRepository.findAll().stream()
                .filter(d -> d.getMedecin().getId().equals(id))
                .toList();
    }
}

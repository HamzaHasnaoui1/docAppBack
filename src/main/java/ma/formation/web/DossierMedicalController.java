package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.DossierMedical;
import ma.formation.repositories.DossierMedicalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
class DossierMedicalController {
    private final DossierMedicalRepository dossierMedicalRepository;

    @GetMapping("/user/dossierMedical")
    public List<DossierMedical> list() { return dossierMedicalRepository.findAll(); }

    @PostMapping("/admin/dossierMedical")
    public DossierMedical create(@RequestBody DossierMedical dossier) { return dossierMedicalRepository.save(dossier); }

    @GetMapping("/admin/dossierMedical/{id}")
    public ResponseEntity<DossierMedical> get(@PathVariable Long id) {
        return dossierMedicalRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}

package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.OrdonnanceMedicamentDTO;
import ma.formation.service.OrdonnanceMedicamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrdonnanceMedicamentController {
    private final OrdonnanceMedicamentService ordonnanceMedicamentService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/ordonnances/{ordonnanceId}/medicaments")
    public ResponseEntity<List<OrdonnanceMedicamentDTO>> getMedicamentsByOrdonnance(@PathVariable Long ordonnanceId) {
        return ResponseEntity.ok(ordonnanceMedicamentService.getMedicamentsByOrdonnance(ordonnanceId));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/ordonnances/{ordonnanceId}/medicaments")
    public ResponseEntity<OrdonnanceMedicamentDTO> ajouterMedicament(
            @PathVariable Long ordonnanceId,
            @Valid @RequestBody OrdonnanceMedicamentDTO dto) {

        return ResponseEntity.ok(ordonnanceMedicamentService.ajouterMedicament(ordonnanceId, dto));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/ordonnances/medicaments/{id}")
    public ResponseEntity<OrdonnanceMedicamentDTO> updateMedicament(
            @PathVariable Long id,
            @Valid @RequestBody OrdonnanceMedicamentDTO dto) {

        return ResponseEntity.ok(ordonnanceMedicamentService.updateMedicament(id, dto));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/ordonnances/medicaments/{id}")
    public ResponseEntity<Void> supprimerMedicament(@PathVariable Long id) {
        ordonnanceMedicamentService.supprimerMedicament(id);
        return ResponseEntity.ok().build();
    }
}
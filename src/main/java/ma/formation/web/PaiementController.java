package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.Paiement;
import ma.formation.repositories.PaiementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementRepository paiementRepository;

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/paiement")
    public ResponseEntity<Paiement> create(@RequestBody Paiement p) {
        Paiement saved = paiementRepository.save(p);
        return ResponseEntity.ok(saved);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/paiement")
    public ResponseEntity<List<Paiement>> list() {
        return ResponseEntity.ok(paiementRepository.findAll());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/paiement/{id}")
    public ResponseEntity<Paiement> getPaiementById(@PathVariable Long id) {
        return paiementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

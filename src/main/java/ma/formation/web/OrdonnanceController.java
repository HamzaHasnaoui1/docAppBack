package ma.formation.web;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.formation.entities.Ordonnance;
import ma.formation.repositories.OrdonnanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
class OrdonnanceController {
    private final OrdonnanceRepository ordonnanceRepository;

    @PostMapping("/admin/ordonnance")
    public Ordonnance create(@RequestBody Ordonnance ordonnance) { return ordonnanceRepository.save(ordonnance); }

    @GetMapping("/user/ordonnance/{id}")
    public ResponseEntity<Ordonnance> get(@PathVariable Long id) {
        return ordonnanceRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}

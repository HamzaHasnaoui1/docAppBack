package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.Paiement;
import ma.formation.repositories.PaiementRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementRepository paiementRepository;

    @PostMapping("/admin/paiement")
    public Paiement create(@RequestBody Paiement p) { return paiementRepository.save(p); }

    @GetMapping("/user/paiement")
    public List<Paiement> list() { return paiementRepository.findAll(); }
}

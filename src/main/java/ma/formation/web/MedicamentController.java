package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.MedicamentDTO;
import ma.formation.entities.Medicament;
import ma.formation.entities.Medecin;
import ma.formation.mappers.MedicamentMapper;
import ma.formation.repositories.MedicamentRepository;
import ma.formation.repositories.MedecinRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MedicamentController {
    private final MedicamentRepository medicamentRepository;
    private final MedicamentMapper medicamentMapper;
    private final MedecinRepository medecinRepository;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/medicaments")
    public ResponseEntity<Map<String, Object>> getAllMedicaments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long medecinId) {

        Page<Medicament> pageMedicaments;
        if (medecinId != null) {
            pageMedicaments = medicamentRepository.searchMedicamentsByMedecin(medecinId, keyword, PageRequest.of(page, size));
        } else {
            pageMedicaments = medicamentRepository.findByNomContainsIgnoreCase(keyword, PageRequest.of(page, size));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("medicaments", medicamentMapper.toDTOs(pageMedicaments.getContent()));
        response.put("currentPage", pageMedicaments.getNumber());
        response.put("totalItems", pageMedicaments.getTotalElements());
        response.put("totalPages", pageMedicaments.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/medicaments/{id}")
    public ResponseEntity<MedicamentDTO> getMedicamentById(@PathVariable Long id) {
        return medicamentRepository.findById(id)
                .map(medicamentMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/medecins/{medecinId}/medicaments")
    public ResponseEntity<Map<String, Object>> getMedicamentsByMedecin(
            @PathVariable Long medecinId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Medicament> pageMedicaments = medicamentRepository.findByMedecinId(medecinId, PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("medicaments", medicamentMapper.toDTOs(pageMedicaments.getContent()));
        response.put("currentPage", pageMedicaments.getNumber());
        response.put("totalItems", pageMedicaments.getTotalElements());
        response.put("totalPages", pageMedicaments.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/medicaments")
    public ResponseEntity<MedicamentDTO> createMedicament(@Valid @RequestBody MedicamentDTO medicamentDTO) {
        // Vérifier si un médicament avec le même nom existe déjà
        if (medicamentRepository.existsByNomIgnoreCase(medicamentDTO.getNom())) {
            return ResponseEntity.badRequest().build();
        }

        Medecin medecin = medecinRepository.findById(medicamentDTO.getMedecinId())
                .orElseThrow(() -> new EntityNotFoundException("Medecin not found with id: " + medicamentDTO.getMedecinId()));

        Medicament medicament = medicamentMapper.toEntity(medicamentDTO);
        medicament.setMedecin(medecin);
        Medicament savedMedicament = medicamentRepository.save(medicament);

        return ResponseEntity.ok(medicamentMapper.toDTO(savedMedicament));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/medicaments/{id}")
    public ResponseEntity<MedicamentDTO> updateMedicament(
            @PathVariable Long id,
            @Valid @RequestBody MedicamentDTO medicamentDTO) {

        if (!medicamentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Medecin medecin = medecinRepository.findById(medicamentDTO.getMedecinId())
                .orElseThrow(() -> new EntityNotFoundException("Medecin not found with id: " + medicamentDTO.getMedecinId()));

        Medicament medicament = medicamentMapper.toEntity(medicamentDTO);
        medicament.setId(id);
        medicament.setMedecin(medecin);
        Medicament updatedMedicament = medicamentRepository.save(medicament);

        return ResponseEntity.ok(medicamentMapper.toDTO(updatedMedicament));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/medicaments/{id}")
    public ResponseEntity<Void> deleteMedicament(@PathVariable Long id) {
        if (!medicamentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        medicamentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
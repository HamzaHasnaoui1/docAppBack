package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.ConsultationDTO;
import ma.formation.repositories.ConsultationRepository;
import ma.formation.service.ConsultationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ConsultationController {
    private final ConsultationService consultationService;
    private final ConsultationRepository consultationRepository;


    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/consultations")
    public ResponseEntity<?> getConsultations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<ConsultationDTO> pageConsultations = consultationService.searchConsultations(keyword, page, size);

        var response = new HashMap<String, Object>();
        response.put("consultations", pageConsultations.getContent());
        response.put("totalPages", pageConsultations.getTotalPages());
        response.put("currentPage", page);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/consultations/all")
    public ResponseEntity<List<ConsultationDTO>> getAllConsultations() {
        return ResponseEntity.ok(consultationService.getAllConsultations());
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/consultations/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/consultations")
    public ResponseEntity<ConsultationDTO> saveConsultation(
            @Valid @RequestBody ConsultationDTO consultationDTO,
            @RequestParam(name = "rendezVousId") Long rendezVousId) {
        return ResponseEntity.ok(consultationService.createConsultation(consultationDTO, rendezVousId));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/consultations/{id}")
    public ResponseEntity<ConsultationDTO> updateConsultation(
            @PathVariable Long id,
            @Valid @RequestBody ConsultationDTO consultationDTO) {
        return ResponseEntity.ok(consultationService.updateConsultation(id, consultationDTO));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/consultations/form-data")
    public ResponseEntity<Map<String, Object>> getFormData() {
        return ResponseEntity.ok(consultationService.getConsultationFormData());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping({"/admin/consultations/{id}", "/user/consultations/{id}"})
    public ResponseEntity<ConsultationDTO> getConsultationById(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationById(id));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/consultations/{id}/facture")
    public ResponseEntity<ConsultationDTO> getConsultationForFacture(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationById(id));
    }
}
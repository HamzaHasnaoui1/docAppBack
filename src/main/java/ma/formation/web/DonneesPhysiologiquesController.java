package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.DonneesPhysiologiquesDTO;
import ma.formation.service.DonneesPhysiologiquesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DonneesPhysiologiquesController {
    private final DonneesPhysiologiquesService donneesPhysiologiquesService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/rdv/{rendezVousId}/donnees-physiologiques")
    public ResponseEntity<DonneesPhysiologiquesDTO> getDonneesPhysiologiquesByRendezVous(@PathVariable Long rendezVousId) {
        DonneesPhysiologiquesDTO donnees = donneesPhysiologiquesService.getDonneesPhysiologiquesByRendezVous(rendezVousId);
        return donnees != null ? ResponseEntity.ok(donnees) : ResponseEntity.notFound().build();
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/user/rdv/{rendezVousId}/donnees-physiologiques")
    public ResponseEntity<DonneesPhysiologiquesDTO> saveDonneesPhysiologiques(
            @PathVariable Long rendezVousId,
            @Valid @RequestBody DonneesPhysiologiquesDTO donneesDTO) {

        DonneesPhysiologiquesDTO savedDonnees = donneesPhysiologiquesService.saveDonneesPhysiologiques(donneesDTO, rendezVousId);
        return ResponseEntity.ok(savedDonnees);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{patientId}/donnees-physiologiques/latest")
    public ResponseEntity<List<DonneesPhysiologiquesDTO>> getLatestDonneesPhysiologiques(@PathVariable Long patientId) {
        List<DonneesPhysiologiquesDTO> donnees = donneesPhysiologiquesService.getLatestDonneesPhysiologiquesByPatient(patientId);
        return ResponseEntity.ok(donnees);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/patients/{patientId}/donnees-physiologiques/history")
    public ResponseEntity<List<DonneesPhysiologiquesDTO>> getDonneesPhysiologiquesHistory(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        List<DonneesPhysiologiquesDTO> donnees = donneesPhysiologiquesService
                .getDonneesPhysiologiquesByPatientAndDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(donnees);
    }
}
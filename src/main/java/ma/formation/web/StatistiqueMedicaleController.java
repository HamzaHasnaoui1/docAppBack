// src/main/java/ma/formation/web/StatistiqueMedicaleController.java
package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.service.StatistiqueMedicaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats/medical")
@CrossOrigin("*")
@RequiredArgsConstructor
public class StatistiqueMedicaleController {
    private final StatistiqueMedicaleService statistiqueMedicaleService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/patients/{patientId}/rapport-sante")
    public ResponseEntity<Map<String, Object>> getRapportSante(@PathVariable Long patientId) {
        return ResponseEntity.ok(statistiqueMedicaleService.genererRapportSante(patientId));
    }
}
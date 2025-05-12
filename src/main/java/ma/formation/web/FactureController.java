package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.Facture;
import ma.formation.service.FactureService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FactureController {
    private final FactureService factureService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/factures/consultation/{consultationId}")
    public ResponseEntity<byte[]> generateFacture(
            @PathVariable Long consultationId,
            @RequestParam(defaultValue = "false") boolean withTVA,
            @RequestParam(defaultValue = "inline") String dispositionType) {

        byte[] pdf = factureService.generateFacturePdf(consultationId, withTVA);

        ContentDisposition contentDisposition = ContentDisposition.builder(dispositionType)
                .filename("facture_consultation_" + consultationId + ".pdf")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(pdf);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/admin/factures")
    public ResponseEntity<List<Facture>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/factures/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }
}
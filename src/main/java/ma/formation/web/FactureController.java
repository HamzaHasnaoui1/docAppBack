package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.service.FactureService;
import ma.formation.service.IHospitalServiceImpl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/factures")
@AllArgsConstructor
@CrossOrigin("*")
public class FactureController {
    private final FactureService factureService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/factures")
    public ResponseEntity<byte[]> generateFacture(
            @RequestParam(required = false) Long patientId,
            @RequestParam(defaultValue = "inline") String dispositionType) {

        byte[] pdf = factureService.generateFacturePdf(patientId);

        ContentDisposition contentDisposition = ContentDisposition.builder(dispositionType)
                .filename("facture_" + (patientId != null ? patientId : "") + ".pdf")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(pdf);
    }
}
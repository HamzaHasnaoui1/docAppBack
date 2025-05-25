package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.Facture;
import ma.formation.security.annotations.RequirePermission;
import ma.formation.service.FactureService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class FactureController {
    private final FactureService factureService;

    @GetMapping("/user/factures")
    @RequirePermission("FACTURE_CONSULTER")
    public ResponseEntity<List<Facture>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/user/factures/{id}")
    @RequirePermission("FACTURE_CONSULTER")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @GetMapping("/user/factures/{consultationId}/pdf")
    @RequirePermission("FACTURE_GENERER")
    public ResponseEntity<byte[]> generateFacturePdf(@PathVariable Long consultationId, 
                                                    @RequestParam(defaultValue = "false") boolean withTVA,
                                                    @RequestParam(defaultValue = "true") boolean inline) {
        byte[] pdfBytes = factureService.generateFacturePdf(consultationId, withTVA);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Définir si le PDF doit être affiché dans le navigateur (inline) ou téléchargé (attachment)
        String contentDisposition = inline 
            ? "inline; filename=facture.pdf" 
            : "attachment; filename=facture.pdf";
            
        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
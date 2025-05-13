package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.dtos.DocumentDTO;
import ma.formation.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Télécharger un document pour un dossier médical
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/admin/dossiers-medicaux/{dossierMedicalId}/documents")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @PathVariable Long dossierMedicalId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categorie", required = false) String categorie) throws IOException {

        DocumentDTO document = documentService.saveDocument(file, dossierMedicalId, description, categorie);
        return ResponseEntity.ok(document);
    }

    /**
     * Télécharger un document pour un rendez-vous spécifique
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/admin/dossiers-medicaux/{dossierMedicalId}/rdv/{rendezVousId}/documents")
    public ResponseEntity<DocumentDTO> uploadDocumentForRendezVous(
            @PathVariable Long dossierMedicalId,
            @PathVariable Long rendezVousId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categorie", required = false) String categorie) throws IOException {

        DocumentDTO document = documentService.saveDocument(file, dossierMedicalId, rendezVousId, description, categorie);
        return ResponseEntity.ok(document);
    }

    /**
     * Récupérer un document par son ID
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/documents/{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocument(id));
    }

    /**
     * Télécharger le fichier d'un document
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/documents/{id}/fichier")
    public ResponseEntity<Resource> getDocumentFile(@PathVariable Long id) {
        Resource fileResource = documentService.getDocumentFile(id);
        DocumentDTO document = documentService.getDocument(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getNom() + "\"")
                .contentType(MediaType.parseMediaType(document.getType()))
                .body(fileResource);
    }

    /**
     * Récupérer les documents par dossier médical
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/dossiers-medicaux/{dossierMedicalId}/documents")
    public ResponseEntity<Map<String, Object>> getDocumentsByDossierMedical(
            @PathVariable Long dossierMedicalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DocumentDTO> documentsPage = documentService.getDocumentsByDossierMedical(dossierMedicalId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("documents", documentsPage.getContent());
        response.put("currentPage", documentsPage.getNumber());
        response.put("totalItems", documentsPage.getTotalElements());
        response.put("totalPages", documentsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer tous les documents d'un dossier médical (sans pagination)
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/dossiers-medicaux/{dossierMedicalId}/documents/all")
    public ResponseEntity<List<DocumentDTO>> getAllDocumentsByDossierMedical(@PathVariable Long dossierMedicalId) {
        return ResponseEntity.ok(documentService.getDocumentsByDossierMedical(dossierMedicalId));
    }

    /**
     * Récupérer les documents par catégorie
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/documents/categorie/{categorie}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(documentService.getDocumentsByCategorie(categorie));
    }

    /**
     * Rechercher des documents par nom
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/documents/search")
    public ResponseEntity<Map<String, Object>> searchDocuments(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<DocumentDTO> documentsPage = documentService.searchDocuments(keyword, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("documents", documentsPage.getContent());
        response.put("currentPage", documentsPage.getNumber());
        response.put("totalItems", documentsPage.getTotalElements());
        response.put("totalPages", documentsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * Mettre à jour la description d'un document
     */
    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/documents/{id}/description")
    public ResponseEntity<DocumentDTO> updateDocumentDescription(
            @PathVariable Long id,
            @RequestParam String description) {

        return ResponseEntity.ok(documentService.updateDocumentDescription(id, description));
    }

    /**
     * Mettre à jour la catégorie d'un document
     */
    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/documents/{id}/categorie")
    public ResponseEntity<DocumentDTO> updateDocumentCategorie(
            @PathVariable Long id,
            @RequestParam String categorie) {

        return ResponseEntity.ok(documentService.updateDocumentCategorie(id, categorie));
    }

    /**
     * Supprimer un document
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }
}
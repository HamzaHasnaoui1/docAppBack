package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.dtos.DocumentDTO;
import ma.formation.entities.Document;
import ma.formation.entities.DossierMedical;
import ma.formation.entities.RendezVous;
import ma.formation.exceptions.BusinessException;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.mappers.DocumentMapper;
import ma.formation.repositories.DocumentRepository;
import ma.formation.repositories.DossierMedicalRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final RendezVousRepository rendezVousRepository;
    private final DocumentMapper documentMapper;

    @Value("${app.upload.dir:${user.home}/hospital-uploads}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8087}")
    private String baseUrl;

    /**
     * Enregistre un document pour un dossier médical
     */
    @Transactional
    public DocumentDTO saveDocument(MultipartFile file, Long dossierMedicalId, String description, String categorie) throws IOException {
        return saveDocument(file, dossierMedicalId, null, description, categorie);
    }

    /**
     * Enregistre un document pour un dossier médical et un rendez-vous spécifique
     */
    @Transactional
    public DocumentDTO saveDocument(MultipartFile file, Long dossierMedicalId, Long rendezVousId,
                                    String description, String categorie) throws IOException {
        // Vérifier si le dossier médical existe
        DossierMedical dossierMedical = dossierMedicalRepository.findById(dossierMedicalId)
                .orElseThrow(() -> new ResourceNotFoundException("DossierMedical", "id", dossierMedicalId));

        // Vérifier si le rendez-vous existe (s'il est fourni)
        RendezVous rendezVous = null;
        if (rendezVousId != null) {
            rendezVous = rendezVousRepository.findById(rendezVousId)
                    .orElseThrow(() -> new ResourceNotFoundException("RendezVous", "id", rendezVousId));
        }

        // Créer le répertoire de stockage s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom de fichier unique pour éviter les collisions
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Construire le chemin complet du fichier
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Copier le fichier vers le répertoire de stockage
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Créer l'entité Document
        Document document = new Document();
        document.setNom(originalFilename);
        document.setType(file.getContentType());
        document.setDescription(description);
        document.setDateAjout(LocalDateTime.now());
        document.setChemin(uniqueFilename);
        document.setTaille(file.getSize());
        document.setDossierMedical(dossierMedical);
        document.setCategorie(categorie);

        // Sauvegarder dans la base de données
        Document savedDocument = documentRepository.save(document);

        // Convertir en DTO et ajouter l'URL d'accès
        DocumentDTO documentDTO = documentMapper.toDTO(savedDocument);
        documentDTO.setUrlAcces(baseUrl + "/api/documents/" + savedDocument.getId() + "/fichier");

        return documentDTO;
    }

    /**
     * Récupère un document par son ID
     */
    @Transactional(readOnly = true)
    public DocumentDTO getDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        DocumentDTO documentDTO = documentMapper.toDTO(document);
        documentDTO.setUrlAcces(baseUrl + "/api/documents/" + document.getId() + "/fichier");

        return documentDTO;
    }

    /**
     * Récupère le fichier physique d'un document
     */
    public Resource getDocumentFile(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        try {
            Path filePath = Paths.get(uploadDir).resolve(document.getChemin());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new BusinessException("Le fichier n'existe pas ou n'est pas lisible");
            }
        } catch (MalformedURLException e) {
            throw new BusinessException("Erreur lors de la lecture du fichier: " + e.getMessage());
        }
    }

    /**
     * Liste tous les documents d'un dossier médical
     */
    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByDossierMedical(Long dossierMedicalId) {
        if (!dossierMedicalRepository.existsById(dossierMedicalId)) {
            throw new ResourceNotFoundException("DossierMedical", "id", dossierMedicalId);
        }

        List<Document> documents = documentRepository.findByDossierMedicalId(dossierMedicalId);
        List<DocumentDTO> documentDTOs = documentMapper.toDTOs(documents);

        // Ajouter l'URL d'accès à chaque document
        documentDTOs.forEach(dto -> dto.setUrlAcces(baseUrl + "/api/documents/" + dto.getId() + "/fichier"));

        return documentDTOs;
    }

    /**
     * Liste tous les documents d'un dossier médical avec pagination
     */
    @Transactional(readOnly = true)
    public Page<DocumentDTO> getDocumentsByDossierMedical(Long dossierMedicalId, int page, int size) {
        if (!dossierMedicalRepository.existsById(dossierMedicalId)) {
            throw new ResourceNotFoundException("DossierMedical", "id", dossierMedicalId);
        }

        Page<Document> documentsPage = documentRepository.findByDossierMedicalId(dossierMedicalId, PageRequest.of(page, size));
        List<DocumentDTO> documentDTOs = documentMapper.toDTOs(documentsPage.getContent());

        // Ajouter l'URL d'accès à chaque document
        documentDTOs.forEach(dto -> dto.setUrlAcces(baseUrl + "/api/documents/" + dto.getId() + "/fichier"));

        return new PageImpl<>(documentDTOs, documentsPage.getPageable(), documentsPage.getTotalElements());
    }

    /**
     * Liste tous les documents par catégorie
     */
    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByCategorie(String categorie) {
        List<Document> documents = documentRepository.findByCategorie(categorie);
        List<DocumentDTO> documentDTOs = documentMapper.toDTOs(documents);

        // Ajouter l'URL d'accès à chaque document
        documentDTOs.forEach(dto -> dto.setUrlAcces(baseUrl + "/api/documents/" + dto.getId() + "/fichier"));

        return documentDTOs;
    }

    /**
     * Recherche de documents par nom
     */
    @Transactional(readOnly = true)
    public Page<DocumentDTO> searchDocuments(String keyword, int page, int size) {
        Page<Document> documentsPage = documentRepository.findByNomContainingIgnoreCase(keyword, PageRequest.of(page, size));
        List<DocumentDTO> documentDTOs = documentMapper.toDTOs(documentsPage.getContent());

        // Ajouter l'URL d'accès à chaque document
        documentDTOs.forEach(dto -> dto.setUrlAcces(baseUrl + "/api/documents/" + dto.getId() + "/fichier"));

        return new PageImpl<>(documentDTOs, documentsPage.getPageable(), documentsPage.getTotalElements());
    }

    /**
     * Mise à jour de la description d'un document
     */
    @Transactional
    public DocumentDTO updateDocumentDescription(Long id, String description) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        document.setDescription(description);
        Document updatedDocument = documentRepository.save(document);

        DocumentDTO documentDTO = documentMapper.toDTO(updatedDocument);
        documentDTO.setUrlAcces(baseUrl + "/api/documents/" + updatedDocument.getId() + "/fichier");

        return documentDTO;
    }

    /**
     * Mise à jour de la catégorie d'un document
     */
    @Transactional
    public DocumentDTO updateDocumentCategorie(Long id, String categorie) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        document.setCategorie(categorie);
        Document updatedDocument = documentRepository.save(document);

        DocumentDTO documentDTO = documentMapper.toDTO(updatedDocument);
        documentDTO.setUrlAcces(baseUrl + "/api/documents/" + updatedDocument.getId() + "/fichier");

        return documentDTO;
    }

    /**
     * Suppression d'un document
     */
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        // Supprimer le fichier physique
        try {
            Path filePath = Paths.get(uploadDir).resolve(document.getChemin());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Journaliser l'erreur mais continuer la suppression de l'entrée en base
            System.err.println("Erreur lors de la suppression du fichier physique: " + e.getMessage());
        }

        // Supprimer l'entrée en base de données
        documentRepository.delete(document);
    }
}
package ma.formation.repositories;

import ma.formation.entities.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Trouver les documents par dossier médical
    List<Document> findByDossierMedicalId(Long dossierMedicalId);

    // Trouver les documents par dossier médical avec pagination
    Page<Document> findByDossierMedicalId(Long dossierMedicalId, Pageable pageable);



    // Trouver les documents par catégorie
    List<Document> findByCategorie(String categorie);

    // Trouver les documents par dossier médical et catégorie
    List<Document> findByDossierMedicalIdAndCategorie(Long dossierMedicalId, String categorie);

    // Trouver les documents par dossier médical et période
    @Query("SELECT d FROM Document d WHERE d.dossierMedical.id = :dossierMedicalId AND d.dateAjout BETWEEN :debut AND :fin")
    List<Document> findByDossierMedicalIdAndDateAjoutBetween(
            @Param("dossierMedicalId") Long dossierMedicalId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // Recherche par nom (contient)
    Page<Document> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    // Recherche par type de fichier
    List<Document> findByType(String type);
}
package ma.formation.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private Long id;
    private String nom;
    private String type;
    private String description;
    private LocalDateTime dateAjout;
    private Long taille;
    private Long dossierMedicalId;
    private Long rendezVousId;
    private String categorie;

    // Champ utile pour l'affichage côté client
    private String urlAcces;
}
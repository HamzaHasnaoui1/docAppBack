package ma.formation.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String type; // Type MIME du fichier

    private String description;

    @Column(nullable = false)
    private LocalDateTime dateAjout;

    @Column(nullable = false)
    private String chemin; // Chemin vers le fichier physique ou identifiant dans un stockage

    private Long taille; // Taille du fichier en octets

    // Relation avec le dossier médical
    @ManyToOne
    @JoinColumn(name = "dossier_medical_id")
    private DossierMedical dossierMedical;

    // Catégorie du document (ex: "resultat_analyse", "compte_rendu", "radio", etc.)
    private String categorie;
}
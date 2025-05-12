package ma.formation.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "donnees_physiologiques")
public class DonneesPhysiologiques {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "rendez_vous_id")
    private RendezVous rendezVous;

    // Mesures corporelles
    private Double poids; // en kg
    private Double taille; // en cm
    private Double imc; // Indice de masse corporelle calculé

    // Vision
    private String oeilDroit;
    private String oeilGauche;

    // Tension artérielle
    private Integer tensionSystolique;
    private Integer tensionDiastolique;

    // Autres mesures
    private Integer frequenceCardiaque;
    private Integer frequenceRespiratoire;
    private Double temperature;
    private Double glycemie;

    // Commentaires
    @Column(columnDefinition = "TEXT")
    private String remarques;
}
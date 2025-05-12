package ma.formation.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Ordonnance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    private LocalDate dateEmission;
    private boolean archivee;

    @OneToOne
    @JoinColumn(name = "rendez_vous_id")
    private RendezVous rendezVous;

    // Nouvelle relation avec les médicaments
    @OneToMany(mappedBy = "ordonnance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdonnanceMedicament> medicaments = new ArrayList<>();

    // Remarques générales sur l'ordonnance
    @Column(columnDefinition = "TEXT")
    private String remarques;

    // IMPORTANT: Suppression des champs obsolètes
    // Ces collections seront supprimées car elles sont remplacées par la relation avec OrdonnanceMedicament
    /*
    @ElementCollection
    @CollectionTable(
            name = "medicaments_prescrits",
            joinColumns = @JoinColumn(name = "ordonnance_id")
    )
    @Column(name = "medicament", columnDefinition = "TEXT")
    private List<String> medicamentsPrescrits = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "instructions_medicaments",
            joinColumns = @JoinColumn(name = "ordonnance_id")
    )
    @MapKeyColumn(name = "medicament_nom")
    @Column(name = "posologie", columnDefinition = "TEXT")
    private java.util.Map<String, String> posologies = new java.util.HashMap<>();
    */

    // Méthode pour ajouter un médicament
    public void ajouterMedicament(Medicament medicament, String posologie, String duree, String frequence, String instructions) {
        OrdonnanceMedicament ordonnanceMedicament = new OrdonnanceMedicament();
        ordonnanceMedicament.setOrdonnance(this);
        ordonnanceMedicament.setMedicament(medicament);
        ordonnanceMedicament.setPosologie(posologie);
        ordonnanceMedicament.setDuree(duree);
        ordonnanceMedicament.setFrequence(frequence);
        ordonnanceMedicament.setInstructions(instructions);

        this.medicaments.add(ordonnanceMedicament);
    }

    // Méthode pour supprimer un médicament
    public void supprimerMedicament(OrdonnanceMedicament medicament) {
        this.medicaments.remove(medicament);
        medicament.setOrdonnance(null);
    }
}
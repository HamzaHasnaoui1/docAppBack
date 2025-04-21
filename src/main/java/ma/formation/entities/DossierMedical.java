package ma.formation.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DossierMedical {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Patient patient;

    @OneToMany(mappedBy = "dossierMedical")
    private List<Consultation> consultations;

    private String allergies;
    private String antecedents;
    private String traitementsChroniques;
}


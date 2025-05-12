package ma.formation.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.formation.enums.GroupeSanguin;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DossierMedical {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String allergies;
    private String antecedents;
    private String traitementsChroniques;

    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguin;

    @OneToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
}


package ma.formation.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.formation.enums.StatusRDV;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateConsultation;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rapport;

    @Enumerated(EnumType.STRING)
    private StatusRDV statusRDV;
    private String prix;

    @OneToOne
    @JoinColumn(name = "rendez_vous_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private RendezVous rendezVous;

    @ManyToOne
    @JoinColumn(name = "dossier_medical_id")
    @JsonBackReference
    private DossierMedical dossierMedical;

    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL)
    private Ordonnance ordonnance;
    //TODO: replace with medicament
}
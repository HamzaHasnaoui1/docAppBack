package ma.formation.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;
import ma.formation.enums.StatusRDV;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateConsultation;
    @Lob
    private String rapport;
    @Enumerated(EnumType.STRING)
    private StatusRDV statusRDV;
    @OneToOne
    @JoinColumn(name = "rendez_vous_id")
    @JsonManagedReference
    private RendezVous rendezVous;
    private String prix ;

    @ManyToOne
    private DossierMedical dossierMedical;

}

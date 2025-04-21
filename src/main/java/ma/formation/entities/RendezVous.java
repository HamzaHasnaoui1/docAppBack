package ma.formation.entities;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import ma.formation.enums.StatusRDV;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class RendezVous {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @Enumerated(EnumType.STRING)
    private StatusRDV statusRDV;
    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Medecin medecin;

    @OneToOne(mappedBy = "rendezVous")
    @JsonBackReference
    private Consultation consultation;

}

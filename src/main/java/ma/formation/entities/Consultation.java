package ma.formation.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateConsultation;
    private String rapport;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private RendezVous rendezVous;
    private String prix ;
    @OneToOne
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Facture facture;

}

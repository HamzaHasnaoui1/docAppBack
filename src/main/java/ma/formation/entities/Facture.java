package ma.formation.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(name = "Factures")
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroFacture;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateFacture;
    private double prixFacture;
}

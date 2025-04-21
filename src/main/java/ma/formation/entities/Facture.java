package ma.formation.entities;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.util.Date;
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
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

package ma.formation.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DisponibiliteMedecin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Medecin medecin;

    private DayOfWeek jourSemaine;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    private boolean disponible;
}

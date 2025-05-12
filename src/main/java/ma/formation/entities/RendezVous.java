package ma.formation.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.formation.enums.StatusRDV;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "rendez_vous") // Explicit table name
public class RendezVous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_rdv") // Match the exact column name in DB
    private StatusRDV statusRDV;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL)
    private Ordonnance ordonnance;


    @Column(columnDefinition = "TEXT")
    private String rapport;

    private double prix;
}
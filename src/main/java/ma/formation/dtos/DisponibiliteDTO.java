package ma.formation.dtos;

import lombok.Data;

import java.time.LocalTime;

@Data
public class DisponibiliteDTO {
    private Long id;
    private Long medecinId;
    private String jourSemaine;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private boolean disponible;
}

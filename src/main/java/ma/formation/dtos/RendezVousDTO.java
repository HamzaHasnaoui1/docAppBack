package ma.formation.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class RendezVousDTO {
    private Long id;
    private Date date;
    private String statusRDV;
    private MedecinDTO medecin;
    private ConsultationDTO consultation;
}
package ma.formation.dtos;

import lombok.Data;

import java.util.Date;
@Data
public class ConsultationDTO {
    private Long id;
    private Date dateConsultation;
    private String rapport;
    private String statusRDV;
    private String prix;
    private OrdonnanceDTO ordonnance;
    private RendezVousDTO rendezVous;
}
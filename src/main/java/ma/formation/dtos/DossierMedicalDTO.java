package ma.formation.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DossierMedicalDTO {
    private Long id;
    private String allergies;
    private String antecedents;
    private String traitementsChroniques;
    private List<ConsultationDTO> consultations;
}

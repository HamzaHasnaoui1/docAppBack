package ma.formation.dtos;

import lombok.Data;

@Data
public class DossierMedicalDTO {
    private Long id;
    private String allergies;
    private String antecedents;
    private String traitementsChroniques;
    private String groupeSanguin;
}
package ma.formation.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DossierMedicalDTO {
    private Long id;
    private String allergies;
    private String antecedents;
    private String traitementsChroniques;
    private String groupeSanguin;
    private List<DocumentDTO> documents = new ArrayList<>();
}
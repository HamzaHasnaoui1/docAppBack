package ma.formation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdonnanceRequest {
    private String contenu;
    private String remarques;
    private Boolean archivee;
    private List<OrdonnanceMedicamentDTO> medicaments;
} 
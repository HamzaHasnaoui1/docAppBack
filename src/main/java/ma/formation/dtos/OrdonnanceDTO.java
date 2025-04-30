package ma.formation.dtos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OrdonnanceDTO {
    private Long id;
    private String contenu;
    private LocalDate dateEmission;
    private boolean archivee;
    private String remarques;
   // private RendezVousDTO rendezVous;

    // Nouvelle liste des médicaments associés
    private List<OrdonnanceMedicamentDTO> medicaments = new ArrayList<>();

}
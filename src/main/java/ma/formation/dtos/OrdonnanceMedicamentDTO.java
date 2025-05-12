package ma.formation.dtos;

import lombok.Data;

@Data
public class OrdonnanceMedicamentDTO {
    private Long id;
    private Long ordonnanceId;
    private MedicamentDTO medicament;
    private String posologie;
    private String duree;
    private String frequence;
    private String instructions;
}
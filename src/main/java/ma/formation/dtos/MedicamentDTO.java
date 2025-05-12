package ma.formation.dtos;

import lombok.Data;

@Data
public class MedicamentDTO {
    private Long id;
    private String nom;
    private String description;
    private String categorie;
    private String fabricant;
    private String dosageStandard;
    private boolean actif;
}
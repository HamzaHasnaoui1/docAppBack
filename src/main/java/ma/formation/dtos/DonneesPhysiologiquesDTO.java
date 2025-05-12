package ma.formation.dtos;

import lombok.Data;

@Data
public class DonneesPhysiologiquesDTO {
    private Long id;
    private Long rendezVousId;

    private Double poids;
    private Double taille;
    private Double imc;

    private String oeilDroit;
    private String oeilGauche;

    private Integer tensionSystolique;
    private Integer tensionDiastolique;

    private Integer frequenceCardiaque;
    private Integer frequenceRespiratoire;
    private Double temperature;
    private Double glycemie;

    private String remarques;
}
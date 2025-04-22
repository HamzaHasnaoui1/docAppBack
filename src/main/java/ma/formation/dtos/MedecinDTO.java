package ma.formation.dtos;

import lombok.Data;

@Data
public class MedecinDTO {
    private Long id;
    private String nom;
    private String email;
    private String specialite;
    private String numeroTelephone;
}

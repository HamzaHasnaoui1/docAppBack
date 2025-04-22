package ma.formation.dtos;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PatientDTO {
    private Long id;
    private String nom;
    private Date dateNaissance;
    private boolean malade;
    private String adresse;
    private String codePostal;
    private String numeroTelephone;
    private String titre;
    private String rapport;
    private DossierMedicalDTO dossierMedical;
    private List<RendezVousDTO> rendezVousList;
}
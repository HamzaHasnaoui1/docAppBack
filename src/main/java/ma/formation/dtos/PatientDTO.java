package ma.formation.dtos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PatientDTO {
    private Long id;
    private String nom;
    private Date dateNaissance;
    private boolean malade;
    private String adresse;
    private String cin;
    private String email;
    private String codePostal;
    private String numeroTelephone;
    private String titre;
    private String rapport;
    private DossierMedicalDTO dossierMedical;

    private List<RendezVousDTO> rendezVousList;
}
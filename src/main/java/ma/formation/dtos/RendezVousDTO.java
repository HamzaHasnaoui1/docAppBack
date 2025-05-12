package ma.formation.dtos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.Date;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RendezVousDTO {
    private Long id;
    private Date date;
    private Date dateFin;
    private String statusRDV;
    private MedecinDTO medecin;
    private OrdonnanceDTO ordonnance;
    private PatientDTO patient;
    private String rapport;
    private double prix;
}
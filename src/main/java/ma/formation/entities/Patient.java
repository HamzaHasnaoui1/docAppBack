package ma.formation.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity // entite jpa qui a un id
@Data // lombok ajout les getters et setters
@AllArgsConstructor
@NoArgsConstructor

public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Size(min = 3,max = 10)
    private String nom;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateNaissance;
    private boolean malade;
    private String isMalade;
    private String adresse;
    private String codePostal;
    private String numeroTelephone;
    private Titre titre;

    public Patient(String nom, Date dateNaissance, boolean malade, String adresse, String codePostal, String numeroTelephone, Titre titre) {
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.malade = malade;
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.numeroTelephone = numeroTelephone;
        this.titre = titre;
    }


}

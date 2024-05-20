package ma.formation.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Medecin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String email;
    private String specialite;
    private String numeroTelephone;

    public Medecin(String nom, String email, String specialite, String numeroTelephone) {
        this.nom = nom;
        this.email = email;
        this.specialite = specialite;
        this.numeroTelephone = numeroTelephone;
    }
}

package ma.formation.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.formation.entities.Medecin;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppUser {
    @Id
    private String userId;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private String numeroTelephone;
    private boolean active;
    private String image;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_user_app_roles",
            joinColumns = @JoinColumn(name = "app_user_user_id"),
            inverseJoinColumns = @JoinColumn(name = "app_roles_role_id")
    )
    private List<AppRole> appRoles = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;
}

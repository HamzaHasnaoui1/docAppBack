package ma.formation.security.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.formation.security.entities.AppRole;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppRoleRepository;
import ma.formation.security.repositories.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service // annotation service
@Slf4j //annotation lombok permet de donner un attribut qui s'appel log qui permet de logger
@AllArgsConstructor
// pour des raison de security l'utilisation d'autowired n'est pas recommendé => utiliser un constructeur
@Transactional //a la fin =>commit
public class SecurityServiceImpl implements SecurityService {
    private AppUserRepository appUserReository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser saveNewUser(String username, String password, String verifyPassword) {
        if (!password.equals(verifyPassword)) throw new RuntimeException("Mot de passe ne correspond pas");
        String hashedPWD = passwordEncoder.encode(password); // hasher le mdp
        AppUser appUser = new AppUser();
        appUser.setUserId(UUID.randomUUID().toString()); //pour generer un id
        //UUID => genere des chaines de caractere aleatoire qui depend de la date systeme
        appUser.setUsername(username);
        appUser.setPassword(hashedPWD);
        appUser.setActive(true);
        AppUser savedAppUser = appUserReository.save(appUser);
        return savedAppUser;
    }

    @Override
    public AppRole saveNewRole(String roleName, String description) {
        AppRole appRole = appRoleRepository.findByRoleName(roleName); // verifie si le role exist deja
        if (appRole != null) throw new RuntimeException("Role " + roleName + " exist deja");
        appRole = new AppRole();
        appRole.setRoleName(roleName);
        appRole.setDescription(description);
        AppRole savedAppRole = appRoleRepository.save(appRole);
        return savedAppRole;
    }

    @Override // affecter le role a l'utilisateur
    public void addRoleToUser(String username, String roleName) {
        AppUser appUser = appUserReository.findByUsername(username); // charger l'user
        if (appUser == null) throw new RuntimeException("User not found");
        AppRole appRole = appRoleRepository.findByRoleName(roleName); // charger le role
        if (appRole == null) throw new RuntimeException("Role not found");
        appUser.getAppRoles().add(appRole);// ajouter le role dans la collection des roles de appUser
        appUserReository.save(appUser);// n'est pas necessaire

    }

    @Override
    public void removeRoleFromUser(String username, String roleName) {
        AppUser appUser = appUserReository.findByUsername(username); // charger l'user
        if (appUser == null) throw new RuntimeException("User not found");
        AppRole appRole = appRoleRepository.findByRoleName(roleName); // charger le role
        if (appRole == null) throw new RuntimeException("Role not found");
        appUser.getAppRoles().remove(appRole);// ajouter le role dans la collection des roles de appUser
    }


    @Override
    public AppUser loadUserByUsername(String username) {

        return appUserReository.findByUsername(username);
    }
}

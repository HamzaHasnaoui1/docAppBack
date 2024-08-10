package ma.formation.security.service;


import ma.formation.security.entities.AppRole;
import ma.formation.security.entities.AppUser;

public interface SecurityService {
    AppUser saveNewUser(String username,String email, String numeroTelephone, String password, String verifyPassword);
    AppRole saveNewRole(String roleName, String description);
    void addRoleToUser(String username, String roleName);
    AppUser loadUserByUsername(String username);//chercher un utilisateur
    void removeRoleFromUser(String username, String roleName);

}

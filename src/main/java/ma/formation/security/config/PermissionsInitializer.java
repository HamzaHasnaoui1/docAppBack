package ma.formation.security.config;

import lombok.RequiredArgsConstructor;
import ma.formation.security.entities.AppRole;
import ma.formation.security.entities.Permission;
import ma.formation.security.repositories.AppRoleRepository;
import ma.formation.security.repositories.PermissionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PermissionsInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final AppRoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Définir les permissions de base par catégorie
        initializePermissions();
        
        // Assigner les permissions à l'admin
        assignPermissionsToAdmin();
    }
    
    private void initializePermissions() {
        // Catégorie: Ordonnance
        createPermissionIfNotExists("ORDONNANCE_GENERER", "Générer une ordonnance", "ORDONNANCE");
        createPermissionIfNotExists("ORDONNANCE_CONSULTER", "Consulter une ordonnance", "ORDONNANCE");
        createPermissionIfNotExists("ORDONNANCE_MODIFIER", "Modifier une ordonnance", "ORDONNANCE");
        createPermissionIfNotExists("ORDONNANCE_SUPPRIMER", "Supprimer une ordonnance", "ORDONNANCE");
        
        // Catégorie: Médicament
        createPermissionIfNotExists("MEDICAMENT_AJOUTER", "Ajouter un médicament", "MEDICAMENT");
        createPermissionIfNotExists("MEDICAMENT_CONSULTER", "Consulter un médicament", "MEDICAMENT");
        createPermissionIfNotExists("MEDICAMENT_MODIFIER", "Modifier un médicament", "MEDICAMENT");
        createPermissionIfNotExists("MEDICAMENT_SUPPRIMER", "Supprimer un médicament", "MEDICAMENT");
        
        // Catégorie: Gestion des rôles
        createPermissionIfNotExists("ROLE_LIST", "Lister les rôles", "ADMIN");
        createPermissionIfNotExists("ROLE_VIEW", "Consulter un rôle", "ADMIN");
        createPermissionIfNotExists("ROLE_CREATE", "Créer un rôle", "ADMIN");
        createPermissionIfNotExists("ROLE_UPDATE", "Mettre à jour un rôle", "ADMIN");
        createPermissionIfNotExists("ROLE_DELETE", "Supprimer un rôle", "ADMIN");
        
        // Catégorie: Gestion des permissions
        createPermissionIfNotExists("PERMISSION_LIST", "Lister les permissions", "ADMIN");
        createPermissionIfNotExists("PERMISSION_VIEW", "Consulter une permission", "ADMIN");
        createPermissionIfNotExists("PERMISSION_CREATE", "Créer une permission", "ADMIN");
        createPermissionIfNotExists("PERMISSION_UPDATE", "Mettre à jour une permission", "ADMIN");
        createPermissionIfNotExists("PERMISSION_DELETE", "Supprimer une permission", "ADMIN");
        createPermissionIfNotExists("ROLE_PERMISSION_ASSIGN", "Assigner une permission à un rôle", "ADMIN");
        createPermissionIfNotExists("ROLE_PERMISSION_REMOVE", "Retirer une permission d'un rôle", "ADMIN");
    }
    
    private Permission createPermissionIfNotExists(String name, String description, String category) {
        Optional<Permission> existingPermission = permissionRepository.findByName(name);
        if (existingPermission.isPresent()) {
            return existingPermission.get();
        }
        
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        permission.setCategory(category);
        return permissionRepository.save(permission);
    }
    
    private void assignPermissionsToAdmin() {
        Optional<AppRole> adminRoleOpt = roleRepository.findByRoleName("ADMIN");
        if (adminRoleOpt.isEmpty()) {
            return; // Le rôle admin n'existe pas encore
        }
        
        AppRole adminRole = adminRoleOpt.get();
        List<Permission> allPermissions = permissionRepository.findAll();
        adminRole.setPermissions(allPermissions);
        roleRepository.save(adminRole);
        
        // Assigner un sous-ensemble de permissions au rôle USER
        Optional<AppRole> userRoleOpt = roleRepository.findByRoleName("USER");
        if (userRoleOpt.isPresent()) {
            AppRole userRole = userRoleOpt.get();
            List<Permission> userPermissions = permissionRepository.findByCategory("ORDONNANCE");
            userPermissions.addAll(permissionRepository.findByName("MEDICAMENT_CONSULTER").stream().toList());
            userRole.setPermissions(userPermissions);
            roleRepository.save(userRole);
        }
    }
} 
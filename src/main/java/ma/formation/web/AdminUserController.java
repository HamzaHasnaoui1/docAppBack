package ma.formation.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.security.annotations.RequirePermission;
import ma.formation.security.dtos.RoleDTO;
import ma.formation.security.dtos.UserDetailsResponse;
import ma.formation.security.dtos.UserRolesRequest;
import ma.formation.security.entities.AppRole;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppRoleRepository;
import ma.formation.security.repositories.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;

    @GetMapping("/users")
    @RequirePermission("ROLE_LIST")
    public ResponseEntity<List<UserDetailsResponse>> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        List<AppUser> users = userRepository.findAll();
        List<UserDetailsResponse> response = users.stream()
                .map(this::mapToUserDetailsResponse)
                .collect(Collectors.toList());
        log.debug("Utilisateurs retournés: {}", users.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    @RequirePermission("ROLE_VIEW")
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable String id) {
        log.info("Récupération de l'utilisateur avec ID: {}", id);
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(mapToUserDetailsResponse(user));
    }

    @PutMapping("/users/{id}/roles")
    @RequirePermission("ROLE_PERMISSION_ASSIGN")
    public ResponseEntity<UserDetailsResponse> updateUserRoles(
            @PathVariable String id,
            @RequestBody UserRolesRequest request) {
        
        log.info("Mise à jour des rôles pour l'utilisateur avec ID: {}", id);
        log.debug("Nouveaux rôles: {}", request.getRoles());
        
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        List<AppRole> roles = new ArrayList<>();
        for (String roleName : request.getRoles()) {
            AppRole role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));
            roles.add(role);
        }
        
        user.setAppRoles(roles);
        AppUser updatedUser = userRepository.save(user);
        log.info("Rôles mis à jour avec succès pour l'utilisateur: {}", user.getUsername());
        
        return ResponseEntity.ok(mapToUserDetailsResponse(updatedUser));
    }

    private UserDetailsResponse mapToUserDetailsResponse(AppUser user) {
        List<String> roles = user.getAppRoles().stream()
                .map(AppRole::getRoleName)
                .collect(Collectors.toList());
        
        List<String> permissions = user.getAppRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .distinct()
                .collect(Collectors.toList());
        
        UserDetailsResponse response = new UserDetailsResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getNumeroTelephone(),
                user.isActive(),
                roles,
                permissions
        );
        
        log.debug("Mapped user {} to response with userId: {}", user.getUsername(), response.getUserId());
        return response;
    }
} 
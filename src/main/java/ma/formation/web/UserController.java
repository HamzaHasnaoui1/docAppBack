package ma.formation.web;

import lombok.AllArgsConstructor;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppUserRepository;
import ma.formation.security.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final AppUserRepository appUserRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user/profile")
    public ResponseEntity<AppUser> getCurrentUserProfile() {
        AppUser currentUser = userDetailsService.getCurrentAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié"));
        return ResponseEntity.ok(currentUser);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/user/profile/edit")
    public ResponseEntity<AppUser> updateCurrentUser(@RequestBody AppUser updatedUser) {
        AppUser currentUser = userDetailsService.getCurrentAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié"));

        currentUser.setEmail(updatedUser.getEmail());

        AppUser savedUser = appUserRepository.save(currentUser);
        return ResponseEntity.ok(savedUser);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/users")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(appUserRepository.findAll());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/users/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(appUserRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé")));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/users/{id}/edit")
    public ResponseEntity<AppUser> updateUser(
            @PathVariable String id,
            @RequestBody AppUser updatedUser) {

        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setActive(updatedUser.isActive());
        existingUser.setNumeroTelephone(updatedUser.getNumeroTelephone());
        existingUser.setImage(updatedUser.getImage());

        if (updatedUser.getAppRoles() != null) {
            existingUser.setAppRoles(new ArrayList<>(updatedUser.getAppRoles()));
        }

        AppUser savedUser = appUserRepository.save(existingUser);
        return ResponseEntity.ok(savedUser);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!appUserRepository.existsById(String.valueOf(id))) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        appUserRepository.deleteById(String.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/users/check-username")
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        return ResponseEntity.ok(appUserRepository.existsByUsername(username));
    }

    @GetMapping("/public/users/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        return ResponseEntity.ok(appUserRepository.existsByEmail(email));
    }
}
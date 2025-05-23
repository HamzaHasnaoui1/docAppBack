package ma.formation.security.service;

import lombok.RequiredArgsConstructor;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionCheckService {

    private final AppUserRepository userRepository;

    public boolean currentUserHasPermission(String permissionName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        Optional<AppUser> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            return false;
        }

        AppUser user = userOptional.get();
        return user.getAppRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    public boolean userHasPermission(String username, String permissionName) {
        Optional<AppUser> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            return false;
        }

        AppUser user = userOptional.get();
        return user.getAppRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }
} 
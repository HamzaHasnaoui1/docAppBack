package ma.formation.security.service;

import lombok.RequiredArgsConstructor;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ajouter des logs pour débogage
        System.out.println("Recherche de l'utilisateur: " + username);

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.err.println("Utilisateur non trouvé: " + username);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + username);
                });

        System.out.println("Utilisateur trouvé: " + appUser.getUsername() + ", actif: " + appUser.isActive());

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (appUser.getAppRoles() != null) {
            appUser.getAppRoles().forEach(role -> {
                String roleName = "ROLE_" + role.getRoleName();
                System.out.println("Ajout du rôle: " + roleName);
                authorities.add(new SimpleGrantedAuthority(roleName));
            });
        }

        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                appUser.isActive(),
                true,
                true,
                true,
                authorities
        );
    }
}
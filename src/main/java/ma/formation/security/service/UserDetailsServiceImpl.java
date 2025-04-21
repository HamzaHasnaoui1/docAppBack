package ma.formation.security.service;

import lombok.AllArgsConstructor;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private SecurityService securityService;



    //code qui va se repetter dans tt les app qui utilise spring security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = securityService.loadUserByUsername(username); // chercher l'user dans la bd a l'aide de ma couche service
        // prog imperative
        // avec spring sec les roles doivent etre dans une collection de type GrantedAuthority
        // spring sec consider le role=> obj qui impl l'interface GrantedAuthority

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        //parcourire les roles de appUser
        appUser.getAppRoles().forEach(role->{ // pour chaque role on creer un obj de type GrantedAuthority
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName()); // le role qui se trouve dans la bd on le stock dans un obj de type SimpleGrantedAutority qui implemente l'interface GrantedAutority
            authorities.add(authority);
        });



        //declarative
        //recommendé
        Collection<GrantedAuthority> authorities1 = appUser

                .getAppRoles()
                .stream().map(role-> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());



        // pour retourner un obj de type UserDetails:
        // spring security utilise sa propre classe User et non pas AppUser
        // donc on est obligé de transeferer les données de appUser vers user
        //on va creer un obj de type User qui va prendre les param suivant => username et password de appUser qu'on a chargé a partir de la db
        // le 3eme param => les roles mais ils doivent etre une collection de type GrantedAutority

        User user = new User(appUser.getUsername(),appUser.getPassword(),authorities1);
        return user;
    }

    public Optional<AppUser> getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = authentication.getName();
        return Optional.ofNullable(appUserRepository.findByUsername(username));
    }

}

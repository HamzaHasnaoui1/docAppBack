package ma.formation.security.repositories;

import ma.formation.security.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser,String> {
    AppUser findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

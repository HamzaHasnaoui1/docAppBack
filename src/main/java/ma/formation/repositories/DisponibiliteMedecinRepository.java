package ma.formation.repositories;

import ma.formation.entities.DisponibiliteMedecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibiliteMedecinRepository extends JpaRepository<DisponibiliteMedecin, Long> {
}

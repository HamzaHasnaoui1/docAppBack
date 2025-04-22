package ma.formation.repositories;

import ma.formation.entities.DisponibiliteMedecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibiliteMedecinRepository extends JpaRepository<DisponibiliteMedecin, Long> {
    Page<DisponibiliteMedecin> findByMedecinContains(String nom, Pageable pageable);

}

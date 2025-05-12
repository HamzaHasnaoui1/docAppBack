package ma.formation.repositories;

import ma.formation.entities.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {

    Page<Medecin> findByNomContainingIgnoreCase(String nom, Pageable pageable);

}

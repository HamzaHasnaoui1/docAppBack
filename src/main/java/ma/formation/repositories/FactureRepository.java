package ma.formation.repositories;

import ma.formation.entities.Facture;
import ma.formation.entities.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface FactureRepository extends JpaRepository<Facture , Long> {

}

package ma.formation.repositories;

import ma.formation.entities.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

}

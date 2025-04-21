package ma.formation.repositories;

import ma.formation.entities.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
}

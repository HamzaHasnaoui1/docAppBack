package ma.formation.repositories;

import ma.formation.entities.DossierMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {
}

package ma.formation.repositories;

import ma.formation.entities.DossierMedical;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {
    Page<DossierMedical> findByPatientId(Long patientId, Pageable pageable);
}

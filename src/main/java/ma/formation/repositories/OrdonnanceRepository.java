package ma.formation.repositories;

import ma.formation.entities.Ordonnance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
    Page<Ordonnance> findByConsultation_RendezVous_Patient_NomContains(String keyword, Pageable pageable);
}

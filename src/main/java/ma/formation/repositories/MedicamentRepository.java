package ma.formation.repositories;

import ma.formation.entities.Medicament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    Page<Medicament> findByNomContainsIgnoreCase(String keyword, Pageable pageable);
    boolean existsByNomIgnoreCase(String nom);
}
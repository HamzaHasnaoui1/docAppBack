package ma.formation.repositories;

import ma.formation.entities.Medicament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    Page<Medicament> findByNomContainsIgnoreCase(String keyword, Pageable pageable);
    boolean existsByNomIgnoreCase(String nom);

    Page<Medicament> findByMedecinId(Long medecinId, Pageable pageable);

    @Query("SELECT m FROM Medicament m WHERE m.medecin.id = :medecinId AND " +
            "LOWER(m.nom) LIKE LOWER(concat('%', :keyword, '%'))")
    Page<Medicament> searchMedicamentsByMedecin(@Param("medecinId") Long medecinId, @Param("keyword") String keyword, Pageable pageable);
}
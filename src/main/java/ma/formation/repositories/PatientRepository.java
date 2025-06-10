package ma.formation.repositories;

import ma.formation.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.nom) LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(p.cin) LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(concat('%', :keyword, '%'))")
    Page<Patient> searchPatients(@Param("keyword") String keyword, Pageable pageable);

    Page<Patient> findByMedecinId(Long medecinId, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.medecin.id = :medecinId AND " +
            "(LOWER(p.nom) LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(p.cin) LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(concat('%', :keyword, '%')))")
    Page<Patient> searchPatientsByMedecin(@Param("medecinId") Long medecinId, @Param("keyword") String keyword, Pageable pageable);
}

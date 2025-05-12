// src/main/java/ma/formation/repositories/OrdonnanceRepository.java
package ma.formation.repositories;

import ma.formation.entities.Ordonnance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
    Page<Ordonnance> findByRendezVous_Patient_NomContains(String keyword, Pageable pageable);

    boolean existsByRendezVous_Id(Long consultationId);

    @Query("SELECT o FROM Ordonnance o JOIN o.rendezVous r JOIN r.patient p " +
            "WHERE p.id = :patientId ORDER BY o.dateEmission DESC")
    List<Ordonnance> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT o FROM Ordonnance o JOIN o.rendezVous r JOIN r.patient p " +
            "WHERE p.id = :patientId ORDER BY o.dateEmission DESC")
    Page<Ordonnance> findByPatientIdPageable(@Param("patientId") Long patientId, Pageable pageable);
    
    @Query("SELECT o FROM Ordonnance o JOIN o.medicaments om JOIN om.medicament m " +
            "WHERE LOWER(m.nom) LIKE LOWER(CONCAT('%', :medicament, '%'))")
    List<Ordonnance> findByMedicamentNom(@Param("medicament") String medicament);

}
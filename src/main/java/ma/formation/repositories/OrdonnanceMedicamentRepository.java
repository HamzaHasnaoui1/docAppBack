package ma.formation.repositories;

import ma.formation.entities.OrdonnanceMedicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrdonnanceMedicamentRepository extends JpaRepository<OrdonnanceMedicament, Long> {
    List<OrdonnanceMedicament> findByOrdonnanceId(Long ordonnanceId);

    @Transactional
    @Modifying
    @Query("DELETE FROM OrdonnanceMedicament om WHERE om.ordonnance.id = :ordonnanceId")
    void deleteByOrdonnanceId(@Param("ordonnanceId") Long ordonnanceId);

    @Query("SELECT om FROM OrdonnanceMedicament om WHERE om.medicament.id = :medicamentId")
    List<OrdonnanceMedicament> findByMedicamentId(@Param("medicamentId") Long medicamentId);

    @Query("SELECT DISTINCT om.medicament.id FROM OrdonnanceMedicament om " +
            "JOIN om.ordonnance o JOIN o.rendezVous r JOIN r.patient p " +
            "WHERE p.id = :patientId")
    List<Long> findMedicamentIdsByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT COUNT(om) FROM OrdonnanceMedicament om WHERE om.medicament.id = :medicamentId")
    long countByMedicamentId(@Param("medicamentId") Long medicamentId);
}
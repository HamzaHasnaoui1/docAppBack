package ma.formation.repositories;

import ma.formation.entities.DonneesPhysiologiques;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonneesPhysiologiquesRepository extends JpaRepository<DonneesPhysiologiques, Long> {
    Optional<DonneesPhysiologiques> findByRendezVousId(Long rendezVousId);

    @Query("SELECT d FROM DonneesPhysiologiques d JOIN d.rendezVous r WHERE r.patient.id = :patientId ORDER BY r.date DESC")
    List<DonneesPhysiologiques> findLatestByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT d FROM DonneesPhysiologiques d JOIN d.rendezVous r " +
            "WHERE r.patient.id = :patientId AND r.date BETWEEN :startDate AND :endDate " +
            "ORDER BY r.date ASC")
    List<DonneesPhysiologiques> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
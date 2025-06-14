package ma.formation.repositories;

import ma.formation.entities.RendezVous;
import ma.formation.enums.StatusRDV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    Page<RendezVous> findByDate(Date date, Pageable pageable);

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient LEFT JOIN FETCH r.medecin WHERE r.date = :date ORDER BY r.date DESC")
    List<RendezVous> findByDate(@Param("date") Date date);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.date = :date")
    List<RendezVous> findByMedecinIdAndDate(@Param("medecinId") Long medecinId, @Param("date") Date date);

    @Query("SELECT r FROM RendezVous r " +
            "WHERE r.medecin.id = :medecinId " +
            "AND r.date BETWEEN :dateDebut AND :dateFin")
    List<RendezVous> findByMedecinAndDateBetween(
            @Param("medecinId") Long medecinId,
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin
    );

    List<RendezVous> findByPatient_Id(Long patientId);

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient LEFT JOIN FETCH r.medecin WHERE r.medecin.id = :medecinId ORDER BY r.date DESC")
    List<RendezVous> findByMedecin_Id(@Param("medecinId") Long medecinId);

    Page<RendezVous> findByPatient_Id(Long patientId, Pageable pageable);

    Page<RendezVous> findByMedecin_Id(Long medecinId, Pageable pageable);

    @Query("SELECT r FROM RendezVous r WHERE r.statusRDV = :status")
    List<RendezVous> findByStatus(@Param("status") StatusRDV status);

    @Query("SELECT r FROM RendezVous r WHERE r.date >= :startDate AND r.dateFin <= :endDate")
    List<RendezVous> findByDateBetween(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.statusRDV = :status AND r.date >= :startDate AND r.date <= :endDate")
    Long countByStatusAndDateBetween(
            @Param("status") StatusRDV status,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT r FROM RendezVous r WHERE DATE(r.date) = DATE(:date)")
    List<RendezVous> findByDateOnly(@Param("date") Date date);
}
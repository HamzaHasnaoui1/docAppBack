package ma.formation.repositories;

import ma.formation.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPatientIdAndLuFalseOrderByDateEnvoiDesc(Long patientId);

    Page<Notification> findByPatientIdOrderByDateEnvoiDesc(Long patientId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.patient.id = :patientId AND n.dateEnvoi >= :since ORDER BY n.dateEnvoi DESC")
    List<Notification> findRecentNotificationsByPatientId(
            @Param("patientId") Long patientId,
            @Param("since") LocalDateTime since);

    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.patient.id = :patientId AND n.lu = false")
    void markAllAsRead(@Param("patientId") Long patientId);

    long countByPatientIdAndLuFalse(Long patientId);
}
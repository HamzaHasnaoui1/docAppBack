package ma.formation.repositories;

import ma.formation.dtos.ConsultationDTO;
import ma.formation.entities.Consultation;
import ma.formation.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
   /* @Query("SELECT c FROM Consultation c JOIN c.rendezVous r JOIN r.patient p " +
            "WHERE LOWER(p.nom) LIKE LOWER(concat('%', :nom, '%'))")
    Page<Consultation> findByPatientNomContaining(@Param("nom") String nom, Pageable pageable);*/

    List<Consultation> findByRendezVous_Patient_NomContainingIgnoreCase(String keyword);

    Page<Consultation> findByRendezVous_Patient_NomContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsByRendezVous_Id(Long rendezVousId);
}

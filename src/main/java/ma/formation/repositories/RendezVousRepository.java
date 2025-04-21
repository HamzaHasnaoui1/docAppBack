package ma.formation.repositories;

import ma.formation.entities.RendezVous;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    Page<RendezVous> findByDate(Date date, Pageable pageable);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.date = :date")
    List<RendezVous> findByMedecinIdAndDate(@Param("medecinId") Long medecinId, @Param("date") Date date);

}


package ma.formation.mappers;

import ma.formation.dtos.ConsultationDTO;
import ma.formation.dtos.MedecinDTO;
import ma.formation.dtos.PatientDTO;
import ma.formation.dtos.RendezVousDTO;
import ma.formation.entities.RendezVous;
import ma.formation.enums.StatusRDV;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {MedecinMapper.class, PatientMapper.class, ConsultationMapper.class})
public interface RendezVousMapper {

    RendezVousDTO toDTO(RendezVous rendezVous);

    RendezVous toEntity(RendezVousDTO rendezVousDTO);

    List<RendezVousDTO> toDtos(List<RendezVous> rendezVous);
    List<RendezVous> toEntities(List<RendezVousDTO> rendezVousDTOs);



    @AfterMapping
    default void afterMapping(@MappingTarget RendezVous rendezVous) {
        // Set the rendez-vous reference in the consultation if it exists
        if (rendezVous.getConsultation() != null) {
            rendezVous.getConsultation().setRendezVous(rendezVous);
        }
    }
}
package ma.formation.mappers;

import ma.formation.dtos.ConsultationDTO;
import ma.formation.dtos.OrdonnanceDTO;
import ma.formation.dtos.RendezVousDTO;
import ma.formation.entities.Consultation;
import ma.formation.enums.StatusRDV;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {OrdonnanceMapper.class, RendezVousMapper.class})
public interface ConsultationMapper {
    @Mapping(source = "rendezVous.patient.nom", target = "rendezVous.patient.nom")
    ConsultationDTO toDTO(Consultation consultation);

    Consultation toEntity(ConsultationDTO consultationDTO);

    List<ConsultationDTO> toDtos(List<Consultation> consultations);
    List<Consultation> toEntities(List<ConsultationDTO> consultationDTOs);


    @AfterMapping
    default void afterMapping(@MappingTarget Consultation consultation) {
        // Set the consultation reference in the ordonnance if it exists
        if (consultation.getOrdonnance() != null) {
            consultation.getOrdonnance().setConsultation(consultation);
        }
    }
}
package ma.formation.mappers;

import ma.formation.dtos.ConsultationDTO;
import ma.formation.dtos.OrdonnanceDTO;
import ma.formation.entities.Ordonnance;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = ConsultationMapper.class)
public interface OrdonnanceMapper {

    OrdonnanceDTO toDTO(Ordonnance ordonnance);

    Ordonnance toEntity(OrdonnanceDTO ordonnanceDTO);

    List<OrdonnanceDTO> toDtos(List<Ordonnance> ordonnances);
    List<Ordonnance> toEntities(List<OrdonnanceDTO> ordonnanceDTOs);

    @AfterMapping
    default void afterMapping(@MappingTarget Ordonnance ordonnance) {
        // Set the ordonnance reference in the consultation if it exists
        if (ordonnance.getConsultation() != null) {
            ordonnance.getConsultation().setOrdonnance(ordonnance);
        }
    }
}
package ma.formation.mappers;

import ma.formation.dtos.ConsultationDTO;
import ma.formation.entities.Consultation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsultationMapper {
    ConsultationDTO toDTO(Consultation consultation);

    List<ConsultationDTO> toDTOs(List<Consultation> consultations);

    Consultation toEntity(ConsultationDTO dto);
}

package ma.formation.mappers;

import ma.formation.dtos.DisponibiliteDTO;
import ma.formation.entities.DisponibiliteMedecin;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DisponibiliteMapper {
    DisponibiliteDTO toDTO(DisponibiliteMedecin dispo);

    List<DisponibiliteDTO> toDTOs(List<DisponibiliteMedecin> dispos);

    DisponibiliteMedecin toEntity(DisponibiliteDTO dto);
}

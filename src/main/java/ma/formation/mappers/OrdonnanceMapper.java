package ma.formation.mappers;

import ma.formation.dtos.OrdonnanceDTO;
import ma.formation.entities.Ordonnance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdonnanceMapper {
    OrdonnanceDTO toDTO(Ordonnance ordonnance);

    List<OrdonnanceDTO> toDTOs(List<Ordonnance> ords);

    Ordonnance toEntity(OrdonnanceDTO dto);
}

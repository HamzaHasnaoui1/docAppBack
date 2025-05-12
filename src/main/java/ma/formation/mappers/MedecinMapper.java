package ma.formation.mappers;

import ma.formation.dtos.MedecinDTO;
import ma.formation.entities.Medecin;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedecinMapper {
    MedecinDTO toDTO(Medecin medecin);

    List<MedecinDTO> toDTOs(List<Medecin> medecins);

    Medecin toEntity(MedecinDTO dto);
}

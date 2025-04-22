package ma.formation.mappers;

import ma.formation.dtos.RendezVousDTO;
import ma.formation.entities.RendezVous;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RendezVousMapper {
    RendezVousDTO toDTO(RendezVous rdv);

    List<RendezVousDTO> toDTOs(List<RendezVous> rdvs);

    RendezVous toEntity(RendezVousDTO dto);
}

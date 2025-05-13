package ma.formation.mappers;

import ma.formation.dtos.DonneesPhysiologiquesDTO;
import ma.formation.entities.DonneesPhysiologiques;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DonneesPhysiologiquesMapper {
    @Mapping(source = "rendezVous.id", target = "rendezVousId")
    @Mapping(target = "rendezVousDate", source = "rendezVous.date")
    DonneesPhysiologiquesDTO toDTO(DonneesPhysiologiques donneesPhysiologiques);

    @Mapping(target = "rendezVous", ignore = true)
    List<DonneesPhysiologiquesDTO> toDTOs(List<DonneesPhysiologiques> donneesPhysiologiques);

    @Mapping(target = "rendezVous", ignore = true)
    DonneesPhysiologiques toEntity(DonneesPhysiologiquesDTO dto);
}
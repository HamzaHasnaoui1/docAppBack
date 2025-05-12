package ma.formation.mappers;

import ma.formation.dtos.OrdonnanceMedicamentDTO;
import ma.formation.entities.OrdonnanceMedicament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MedicamentMapper.class})
public interface OrdonnanceMedicamentMapper {
    @Mapping(source = "ordonnance.id", target = "ordonnanceId")
    OrdonnanceMedicamentDTO toDTO(OrdonnanceMedicament ordonnanceMedicament);

    List<OrdonnanceMedicamentDTO> toDTOs(List<OrdonnanceMedicament> ordonnanceMedicaments);

    @Mapping(target = "ordonnance", ignore = true)
    OrdonnanceMedicament toEntity(OrdonnanceMedicamentDTO dto);
}
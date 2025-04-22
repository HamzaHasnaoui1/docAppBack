package ma.formation.mappers;

import ma.formation.dtos.DossierMedicalDTO;
import ma.formation.entities.DossierMedical;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DossierMedicalMapper {
    DossierMedicalDTO toDTO(DossierMedical dossier);

    List<DossierMedicalDTO> toDTOs(List<DossierMedical> dossiers);

    DossierMedical toEntity(DossierMedicalDTO dto);
}

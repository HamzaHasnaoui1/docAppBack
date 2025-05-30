package ma.formation.mappers;

import ma.formation.dtos.MedicamentDTO;
import ma.formation.entities.Medicament;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicamentMapper {
    MedicamentDTO toDTO(Medicament medicament);
    List<MedicamentDTO> toDTOs(List<Medicament> medicaments);
    Medicament toEntity(MedicamentDTO dto);
}
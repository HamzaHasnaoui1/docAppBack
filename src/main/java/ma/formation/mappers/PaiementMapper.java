package ma.formation.mappers;

import ma.formation.dtos.PaiementDTO;
import ma.formation.entities.Paiement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaiementMapper {
    PaiementDTO toDTO(Paiement paiement);

    List<PaiementDTO> toDTOs(List<Paiement> paiements);

    Paiement toEntity(PaiementDTO dto);
}

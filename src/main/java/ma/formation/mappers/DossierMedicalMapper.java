package ma.formation.mappers;

import ma.formation.dtos.DossierMedicalDTO;
import ma.formation.entities.DossierMedical;
import ma.formation.enums.GroupeSanguin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DocumentMapper.class})
public interface DossierMedicalMapper {

    @Mapping(source = "groupeSanguin", target = "groupeSanguin", qualifiedByName = "groupeSanguinToString")
    @Mapping(source = "documents", target = "documents")
    DossierMedicalDTO toDTO(DossierMedical dossier);

    List<DossierMedicalDTO> toDTOs(List<DossierMedical> dossiers);

    @Mapping(source = "groupeSanguin", target = "groupeSanguin", qualifiedByName = "stringToGroupeSanguin")
    @Mapping(target = "documents", ignore = true) // On ignore la conversion des documents lors de la conversion de DTO vers entité
    DossierMedical toEntity(DossierMedicalDTO dto);

    @Named("groupeSanguinToString")
    static String groupeSanguinToString(GroupeSanguin groupeSanguin) {
        return groupeSanguin != null ? groupeSanguin.getLabel() : null;
    }

    // Méthode pour convertir de String à GroupeSanguin (enum)
    @Named("stringToGroupeSanguin")
    static GroupeSanguin stringToGroupeSanguin(String groupeSanguin) {
        try {
            if (groupeSanguin == null || groupeSanguin.isEmpty()) {
                return null;
            }
            return GroupeSanguin.valueOf(groupeSanguin);
        } catch (IllegalArgumentException e) {
            return GroupeSanguin.fromLabel(groupeSanguin);
        }
    }
}
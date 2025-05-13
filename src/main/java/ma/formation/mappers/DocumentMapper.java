package ma.formation.mappers;

import ma.formation.dtos.DocumentDTO;
import ma.formation.entities.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "dossierMedical.id", target = "dossierMedicalId")
    DocumentDTO toDTO(Document document);

    List<DocumentDTO> toDTOs(List<Document> documents);

    @Mapping(target = "dossierMedical", ignore = true)
    Document toEntity(DocumentDTO dto);
}
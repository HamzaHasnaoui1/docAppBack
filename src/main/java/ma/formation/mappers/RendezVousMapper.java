package ma.formation.mappers;

import ma.formation.dtos.RendezVousDTO;
import ma.formation.entities.RendezVous;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {MedecinMapper.class, PatientMapper.class, OrdonnanceMapper.class})
public interface RendezVousMapper {

    @Mapping(source = "ordonnance", target = "ordonnance")
    @Mapping(source = "patient", target = "patient")
    RendezVousDTO toDTO(RendezVous rendezVous);

    @Mapping(source = "ordonnance", target = "ordonnance")
    @Mapping(target = "patient", ignore = true)
    RendezVous toEntity(RendezVousDTO rendezVousDTO);
    @AfterMapping
    default void afterMapping(@MappingTarget RendezVous rendezVous) {
        if (rendezVous.getOrdonnance() != null) {
            rendezVous.getOrdonnance().setRendezVous(rendezVous);
        }
    }

    @Mapping(target = "id", ignore = true) // Prevent ID override
    @Mapping(source = "ordonnance", target = "ordonnance")
    void updateFromDto(RendezVousDTO dto, @MappingTarget RendezVous entity);
}
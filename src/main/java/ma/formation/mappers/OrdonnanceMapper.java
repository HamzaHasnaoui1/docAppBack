package ma.formation.mappers;

import ma.formation.dtos.OrdonnanceDTO;
import ma.formation.entities.Ordonnance;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RendezVousMapper.class, OrdonnanceMedicamentMapper.class})
public interface OrdonnanceMapper {

    @Mapping(source = "medicaments", target = "medicaments")
    @Mapping(target = "rendezVous", ignore = true)
    OrdonnanceDTO toDTO(Ordonnance ordonnance);

    @Mapping(target = "medicaments", ignore = true)
    Ordonnance toEntity(OrdonnanceDTO ordonnanceDTO);

    List<OrdonnanceDTO> toDtos(List<Ordonnance> ordonnances);
    List<Ordonnance> toEntities(List<OrdonnanceDTO> ordonnanceDTOs);

    @AfterMapping
    default void afterMapping(@MappingTarget Ordonnance ordonnance) {
        // Set the ordonnance reference in the consultation if it exists
        if (ordonnance.getRendezVous() != null) {
            ordonnance.getRendezVous().setOrdonnance(ordonnance);
        }

        // S'assurer que les références sont bien établies pour les médicaments
        if (ordonnance.getMedicaments() != null) {
            ordonnance.getMedicaments().forEach(med -> med.setOrdonnance(ordonnance));
        }
    }
}
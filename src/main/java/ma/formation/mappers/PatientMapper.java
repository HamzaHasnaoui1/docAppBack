package ma.formation.mappers;

import ma.formation.dtos.DossierMedicalDTO;
import ma.formation.dtos.PatientDTO;
import ma.formation.dtos.RendezVousDTO;
import ma.formation.entities.DossierMedical;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.enums.Titre;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {DossierMedicalMapper.class, RendezVousMapper.class})
public interface PatientMapper {

    @Mapping(source = "titre", target = "titre", qualifiedByName = "titreToString")
    @Mapping(source = "dossierMedical", target = "dossierMedical")
    @Mapping(source = "rendezVousList", target = "rendezVousList")
    PatientDTO toDTO(Patient patient);

    @Mapping(source = "titre", target = "titre", qualifiedByName = "stringToTitre")
    @Mapping(source = "dossierMedical", target = "dossierMedical")
    Patient toEntity(PatientDTO patientDTO);

    List<PatientDTO> toDtos(List<Patient> patients);
    List<Patient> toEntities(List<PatientDTO> patientDTOs);

    @Named("titreToString")
    static String titreToString(Titre titre) {
        return titre != null ? titre.name() : null;
    }

    @Named("stringToTitre")
    static Titre stringToTitre(String titre) {
        return titre != null ? Titre.valueOf(titre) : null;
    }

    @AfterMapping
    default void afterMapping(@MappingTarget Patient patient) {
        // Set the patient reference in the dossier medical if it exists
        if (patient.getDossierMedical() != null) {
            patient.getDossierMedical().setPatient(patient);
        }

        // Set the patient reference in each rendez-vous
        if (patient.getRendezVousList() != null) {
            patient.getRendezVousList().forEach(rdv -> rdv.setPatient(patient));
        }
    }
}
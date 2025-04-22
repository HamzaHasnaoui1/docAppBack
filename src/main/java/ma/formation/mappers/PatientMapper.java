package ma.formation.mappers;

import ma.formation.dtos.PatientDTO;
import ma.formation.entities.Patient;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDTO toDTO(Patient patient);

    List<PatientDTO> toDTOs(List<Patient> patients);

    Patient toEntity(PatientDTO dto);
}

package ma.formation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.PatientDTO;
import ma.formation.entities.DossierMedical;
import ma.formation.entities.Patient;
import ma.formation.enums.Titre;
import ma.formation.mappers.PatientMapper;
import ma.formation.repositories.DossierMedicalRepository;
import ma.formation.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientMapper patientMapper;

    public PatientDTO createPatientWithDossier(@Valid PatientDTO patientInput) {
        Patient patient = Patient.builder()
                .nom(patientInput.getNom())
                .dateNaissance(patientInput.getDateNaissance())
                .malade(patientInput.isMalade())
                .adresse(patientInput.getAdresse())
                .codePostal(patientInput.getCodePostal())
                .numeroTelephone(patientInput.getNumeroTelephone())
                .titre(Titre.valueOf(patientInput.getTitre()))
                .build();

        Patient savedPatient = patientRepository.save(patient);

        DossierMedical dossier = DossierMedical.builder()
                .patient(savedPatient)
                .allergies("")
                .antecedents("")
                .traitementsChroniques("")
                .build();

        dossierMedicalRepository.save(dossier);
        savedPatient.setDossierMedical(dossier);

        return patientMapper.toDTO(savedPatient);
    }

    public Page<PatientDTO> searchPatients(String keyword, int page, int size) {
        Page<Patient> patients = patientRepository.findByNomContains(keyword, PageRequest.of(page, size));
        List<PatientDTO> dtos = patients.getContent().stream().map(patientMapper::toDTO).toList();
        return new PageImpl<>(dtos, patients.getPageable(), patients.getTotalElements());
    }

    public Optional<PatientDTO> getPatient(Long id) {
        return patientRepository.findById(id).map(patientMapper::toDTO);
    }

    public boolean exists(Long id) {
        return patientRepository.existsById(id);
    }

    public PatientDTO update(Long id, Patient input) {
        Patient updated = Patient.builder()
                .id(id)
                .nom(input.getNom())
                .dateNaissance(input.getDateNaissance())
                .malade(input.isMalade())
                .adresse(input.getAdresse())
                .codePostal(input.getCodePostal())
                .numeroTelephone(input.getNumeroTelephone())
                .titre(input.getTitre())
                .build();

        return patientMapper.toDTO(patientRepository.save(updated));
    }

    public void delete(Long id) {
        patientRepository.deleteById(id);
    }
}

package ma.formation.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.dtos.DossierMedicalDTO;
import ma.formation.dtos.PatientDTO;
import ma.formation.entities.DossierMedical;
import ma.formation.entities.Patient;
import ma.formation.entities.Medecin;
import ma.formation.enums.Titre;
import ma.formation.mappers.PatientMapper;
import ma.formation.repositories.DossierMedicalRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.MedecinRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientMapper patientMapper;
    private final MedecinRepository medecinRepository;

    public PatientDTO createPatientWithDossier(@Valid PatientDTO patientInput) {
        Medecin medecin = medecinRepository.findById(patientInput.getMedecinId())
                .orElseThrow(() -> new EntityNotFoundException("Medecin not found with id: " + patientInput.getMedecinId()));

        Patient patient = Patient.builder()
                .nom(patientInput.getNom())
                .cin(patientInput.getCin())
                .email(patientInput.getEmail())
                .dateNaissance(patientInput.getDateNaissance())
                .malade(patientInput.isMalade())
                .adresse(patientInput.getAdresse())
                .codePostal(patientInput.getCodePostal())
                .numeroTelephone(patientInput.getNumeroTelephone())
                .titre(Titre.valueOf(patientInput.getTitre()))
                .rapport(patientInput.getRapport())
                .medecin(medecin)
                .build();

        Patient savedPatient = patientRepository.save(patient);

        DossierMedicalDTO dossierInput = patientInput.getDossierMedical();
        DossierMedical dossier = DossierMedical.builder()
                .patient(savedPatient)
                .allergies(patientInput.getDossierMedical().getAllergies())
                .antecedents(patientInput.getDossierMedical().getAntecedents())
                .traitementsChroniques(patientInput.getDossierMedical().getTraitementsChroniques())
                .build();

        dossierMedicalRepository.save(dossier);

        savedPatient.setDossierMedical(dossier);

        return patientMapper.toDTO(savedPatient);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> searchPatients(String keyword, int page, int size) {
        Page<Patient> patients = patientRepository.findByNomContainingIgnoreCase(keyword, PageRequest.of(page, size));
        List<PatientDTO> dtos = patients.getContent().stream().map(patientMapper::toDTO).toList();
        return new PageImpl<>(dtos, patients.getPageable(), patients.getTotalElements());
    }

    @Cacheable(value = "patients", key = "'medecin_' + #medecinId")
    public Page<PatientDTO> searchPatientsByMedecin(Long medecinId, String keyword, int page, int size) {
        Page<Patient> patients = patientRepository.searchPatientsByMedecin(medecinId, keyword, PageRequest.of(page, size));
        List<PatientDTO> dtos = patients.getContent().stream().map(patientMapper::toDTO).toList();
        return new PageImpl<>(dtos, patients.getPageable(), patients.getTotalElements());
    }

    public Page<PatientDTO> getPatientsByMedecin(Long medecinId, int page, int size) {
        Page<Patient> patients = patientRepository.findByMedecinId(medecinId, PageRequest.of(page, size));
        List<PatientDTO> dtos = patients.getContent().stream().map(patientMapper::toDTO).toList();
        return new PageImpl<>(dtos, patients.getPageable(), patients.getTotalElements());
    }

    public Page<PatientDTO> globalSearchPatients(String keyword, int page, int size) {
        Page<Patient> patients = patientRepository.searchPatients(keyword, PageRequest.of(page, size));
        List<PatientDTO> dtos = patients.getContent().stream().map(patientMapper::toDTO).toList();
        return new PageImpl<>(dtos, patients.getPageable(), patients.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "patients", key = "#id")
    public boolean exists(Long id) {
        return patientRepository.existsById(id);
    }

    public PatientDTO update(Long id, @Valid PatientDTO patientInput) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));

        existingPatient.setNom(patientInput.getNom());
        existingPatient.setCin(patientInput.getCin());
        existingPatient.setEmail(patientInput.getEmail());
        existingPatient.setDateNaissance(patientInput.getDateNaissance());
        existingPatient.setMalade(patientInput.isMalade());
        existingPatient.setAdresse(patientInput.getAdresse());
        existingPatient.setRapport(patientInput.getRapport());
        existingPatient.setCodePostal(patientInput.getCodePostal());
        existingPatient.setNumeroTelephone(patientInput.getNumeroTelephone());
        existingPatient.setTitre(Titre.valueOf(patientInput.getTitre()));

        DossierMedical existingDossier = existingPatient.getDossierMedical();
        if (existingDossier == null) {
            existingDossier = new DossierMedical();
            existingDossier.setPatient(existingPatient);
        }

        DossierMedicalDTO dossierInput = patientInput.getDossierMedical();
        existingDossier.setAllergies(dossierInput.getAllergies());
        existingDossier.setAntecedents(dossierInput.getAntecedents());
        existingDossier.setTraitementsChroniques(dossierInput.getTraitementsChroniques());

        dossierMedicalRepository.save(existingDossier);
        existingPatient.setDossierMedical(existingDossier);
        Patient updatedPatient = patientRepository.save(existingPatient);

        return patientMapper.toDTO(updatedPatient);
    }

    public void delete(Long id) {
        patientRepository.deleteById(id);
    }
}

// src/main/java/ma/formation/service/DossierMedicalService.java
package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.dtos.DossierMedicalDTO;
import ma.formation.entities.DossierMedical;
import ma.formation.entities.Patient;
import ma.formation.enums.GroupeSanguin;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.mappers.DossierMedicalMapper;
import ma.formation.repositories.DossierMedicalRepository;
import ma.formation.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DossierMedicalService {
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final DossierMedicalMapper dossierMedicalMapper;

    @Transactional(readOnly = true)
    public DossierMedicalDTO getDossierMedical(Long id) {
        DossierMedical dossierMedical = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DossierMedical", "id", id));

        return dossierMedicalMapper.toDTO(dossierMedical);
    }

    @Transactional(readOnly = true)
    public DossierMedicalDTO getDossierMedicalByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        if (patient.getDossierMedical() == null) {
            throw new ResourceNotFoundException("DossierMedical", "patientId", patientId);
        }

        return dossierMedicalMapper.toDTO(patient.getDossierMedical());
    }

    @Transactional(readOnly = true)
    public Page<DossierMedicalDTO> getAllDossiersMedicaux(int page, int size) {
        Page<DossierMedical> dossierPage = dossierMedicalRepository.findAll(PageRequest.of(page, size));
        List<DossierMedicalDTO> dossierDTOs = dossierPage.getContent().stream()
                .map(dossierMedicalMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dossierDTOs, dossierPage.getPageable(), dossierPage.getTotalElements());
    }

    @Transactional
    public DossierMedicalDTO createDossierMedical(DossierMedicalDTO dossierDTO, Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        // Vérifier si le patient a déjà un dossier médical
        if (patient.getDossierMedical() != null) {
            throw new IllegalStateException("Le patient possède déjà un dossier médical");
        }

        DossierMedical dossierMedical = dossierMedicalMapper.toEntity(dossierDTO);
        dossierMedical.setPatient(patient);

        DossierMedical savedDossier = dossierMedicalRepository.save(dossierMedical);
        patient.setDossierMedical(savedDossier);
        patientRepository.save(patient);

        return dossierMedicalMapper.toDTO(savedDossier);
    }

    @Transactional
    public DossierMedicalDTO updateDossierMedical(Long id, DossierMedicalDTO dossierDTO) {
        if (!dossierMedicalRepository.existsById(id)) {
            throw new ResourceNotFoundException("DossierMedical", "id", id);
        }

        DossierMedical dossierMedical = dossierMedicalMapper.toEntity(dossierDTO);
        dossierMedical.setId(id);

        // Récupérer le patient associé pour maintenir la relation
        DossierMedical existingDossier = dossierMedicalRepository.findById(id).get();
        dossierMedical.setPatient(existingDossier.getPatient());

        DossierMedical updatedDossier = dossierMedicalRepository.save(dossierMedical);
        return dossierMedicalMapper.toDTO(updatedDossier);
    }

    @Transactional
    public DossierMedicalDTO updateGroupeSanguin(Long dossierMedicalId, String groupeSanguin) {
        DossierMedical dossierMedical = dossierMedicalRepository.findById(dossierMedicalId)
                .orElseThrow(() -> new ResourceNotFoundException("DossierMedical", "id", dossierMedicalId));

        // Valider et formater le groupe sanguin
        try {
            GroupeSanguin gs = GroupeSanguin.fromLabel(groupeSanguin);
            dossierMedical.setGroupeSanguin(gs);
        } catch (IllegalArgumentException e) {
            // Si le groupe sanguin n'est pas valide, utiliser la valeur brute
            dossierMedical.setGroupeSanguin(GroupeSanguin.valueOf(groupeSanguin));
        }

        DossierMedical updatedDossier = dossierMedicalRepository.save(dossierMedical);
        return dossierMedicalMapper.toDTO(updatedDossier);
    }

    @Transactional
    public DossierMedicalDTO updateAllergies(Long dossierMedicalId, String allergies) {
        DossierMedical dossierMedical = dossierMedicalRepository.findById(dossierMedicalId)
                .orElseThrow(() -> new ResourceNotFoundException("DossierMedical", "id", dossierMedicalId));

        dossierMedical.setAllergies(allergies);
        DossierMedical updatedDossier = dossierMedicalRepository.save(dossierMedical);
        return dossierMedicalMapper.toDTO(updatedDossier);
    }

    @Transactional
    public DossierMedicalDTO updateAntecedents(Long dossierMedicalId, String antecedents) {
        DossierMedical dossierMedical = dossierMedicalRepository.findById(dossierMedicalId)
                .orElseThrow(() -> new ResourceNotFoundException("DossierMedical", "id", dossierMedicalId));

        dossierMedical.setAntecedents(antecedents);
        DossierMedical updatedDossier = dossierMedicalRepository.save(dossierMedical);
        return dossierMedicalMapper.toDTO(updatedDossier);
    }

    @Transactional
    public DossierMedicalDTO updateTraitementsChroniques(Long dossierMedicalId, String traitements) {
        DossierMedical dossierMedical = dossierMedicalRepository.findById(dossierMedicalId)
                .orElseThrow(() -> new ResourceNotFoundException("DossierMedical", "id", dossierMedicalId));

        dossierMedical.setTraitementsChroniques(traitements);
        DossierMedical updatedDossier = dossierMedicalRepository.save(dossierMedical);
        return dossierMedicalMapper.toDTO(updatedDossier);
    }

    @Transactional
    public void deleteDossierMedical(Long id) {
        if (!dossierMedicalRepository.existsById(id)) {
            throw new ResourceNotFoundException("DossierMedical", "id", id);
        }

        // Récupérer le dossier et enlever la relation avec le patient
        DossierMedical dossier = dossierMedicalRepository.findById(id).get();
        if (dossier.getPatient() != null) {
            Patient patient = dossier.getPatient();
            patient.setDossierMedical(null);
            patientRepository.save(patient);
        }

        dossierMedicalRepository.deleteById(id);
    }
}
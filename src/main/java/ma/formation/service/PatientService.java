package ma.formation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.formation.entities.DossierMedical;
import ma.formation.entities.Patient;
import ma.formation.repositories.DossierMedicalRepository;
import ma.formation.repositories.PatientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    @Transactional
    public Patient createPatientWithDossier(Patient patient) {
        Patient savedPatient = patientRepository.save(patient);

        Patient persistedPatient = patientRepository.findById(savedPatient.getId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé après création"));

        DossierMedical dossier = new DossierMedical();
        dossier.setPatient(persistedPatient);
        dossier.setAllergies("");
        dossier.setAntecedents("");
        dossier.setTraitementsChroniques("");
        dossierMedicalRepository.save(dossier);
        persistedPatient.setDossierMedical(dossier);

        return persistedPatient;
    }
}


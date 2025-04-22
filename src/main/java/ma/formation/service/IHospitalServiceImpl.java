package ma.formation.service;

import jakarta.transaction.Transactional;
import ma.formation.entities.*;
import ma.formation.repositories.*;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class IHospitalServiceImpl implements IHopitalService {
    private PatientRepository patientRepository;
    private MedecinRepository medecinRepository;
    private RendezVousRepository rendezVousRepository;
    private ConsultationRepository consultationRepository;
    private FactureRepository factureRepository;

    public IHospitalServiceImpl(PatientRepository patientRepository,
                                MedecinRepository medecinRepository,
                                RendezVousRepository rendezVousRepository,
                                ConsultationRepository consultationRepository,
                                FactureRepository factureRepository) {
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.consultationRepository = consultationRepository;
        this.factureRepository = factureRepository;
    }

    @Override
    public Facture saveFacture(Facture facture) {
        return factureRepository.save((facture));
    }


    @Override
    public Medecin saveMedecin(Medecin medecin) {
        return medecinRepository.save((medecin));
    }

    @Override
    public RendezVous saveRendezVous(RendezVous rendezVous) {
        if (rendezVous.getPatient() == null) {
            Patient patient = patientRepository.getById((Math.random() > 0.5) ? 1L : 2L);
            rendezVous.setPatient(patient);
        }
        if (rendezVous.getMedecin() == null) {
            Medecin medecin = medecinRepository.getById((Math.random() > 0.5) ? ((Math.random() > 0.5) ? 4L : 5L) : ((Math.random() > 0.5) ? 1L : 2L));
            rendezVous.setMedecin(medecin);
        }


        return rendezVousRepository.save(rendezVous);
    }

    @Override
    public Consultation saveConsultation(Consultation consultation) {
        if (consultation.getRendezVous() == null) {
            RendezVous rendezVous = rendezVousRepository.getById((Math.random() > 0.5) ? ((Math.random() > 0.5) ? 4L : 5L) : ((Math.random() > 0.5) ? 1L : 2L));
            consultation.setRendezVous(rendezVous);
        }
        return consultationRepository.save(consultation);
    }

    public String isMalade(Boolean malade) {
        if (malade)
            return "Oui";
        return "Non";
    }
}

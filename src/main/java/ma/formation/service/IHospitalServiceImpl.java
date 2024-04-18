package ma.formation.service;

import ma.formation.entities.Consultation;
import ma.formation.entities.Medecin;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.repositories.ConsultationRepository;
import ma.formation.repositories.MedecinRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class IHospitalServiceImpl implements IHopitalService {
    private PatientRepository patientRepository;
    private MedecinRepository medecinRepository;
    private RendezVousRepository rendezVousRepository;
    private ConsultationRepository consultationRepository;

    public IHospitalServiceImpl(PatientRepository patientRepository,
                                MedecinRepository medecinRepository,
                                RendezVousRepository rendezVousRepository,
                                ConsultationRepository consultationRepository) {
        this.patientRepository = patientRepository;
        this.medecinRepository=medecinRepository;
        this.rendezVousRepository=rendezVousRepository;
        this.consultationRepository=consultationRepository;
    }

    @Override
    public Patient savePatient(Patient patient) {
        return patientRepository.save((patient));
    }

    @Override
    public Medecin saveMedecin(Medecin medecin) {
        return medecinRepository.save((medecin));
    }

    @Override
    public RendezVous saveRendezVous(RendezVous rendezVous) {
        if(rendezVous.getPatient()==null){
            Patient patient= patientRepository.getById((Math.random()>0.5)? 1L:2L);
            rendezVous.setPatient(patient);
        }
        if(rendezVous.getMedecin()==null){
            Medecin medecin= medecinRepository.getById((Math.random()>0.5)? ((Math.random()>0.5)? 4L:5L):((Math.random()>0.5)? 1L:2L));
            rendezVous.setMedecin(medecin);
        }


        return rendezVousRepository.save(rendezVous);
    }

    @Override
    public Consultation saveConsultation(Consultation consultation) {
        if(consultation.getRendezVous()==null) {
            RendezVous rendezVous= rendezVousRepository.getById((Math.random()>0.5)? ((Math.random()>0.5)? 4L:5L):((Math.random()>0.5)? 1L:2L));
            consultation.setRendezVous(rendezVous);}
        return consultationRepository.save(consultation);
    }
    public String isMalade (Boolean malade){
        if (malade)
            return "Oui";
        return "Non";
    }
}

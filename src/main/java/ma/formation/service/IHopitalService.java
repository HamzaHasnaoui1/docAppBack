package ma.formation.service;

import ma.formation.entities.Consultation;
import ma.formation.entities.Medecin;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;

public interface IHopitalService {
    Patient savePatient(Patient patient);
    Medecin saveMedecin(Medecin medecin);
    RendezVous saveRendezVous(RendezVous rendezVous);
    Consultation saveConsultation(Consultation consultation);
}

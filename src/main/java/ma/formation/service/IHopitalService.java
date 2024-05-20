package ma.formation.service;

import ma.formation.entities.*;

public interface IHopitalService {
    Facture saveFacture(Facture facture);
    Patient savePatient(Patient patient);
    Medecin saveMedecin(Medecin medecin);
    RendezVous saveRendezVous(RendezVous rendezVous);
    Consultation saveConsultation(Consultation consultation);
}

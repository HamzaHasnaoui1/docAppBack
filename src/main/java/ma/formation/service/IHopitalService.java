package ma.formation.service;

import ma.formation.entities.Consultation;
import ma.formation.entities.Facture;
import ma.formation.entities.Medecin;
import ma.formation.entities.RendezVous;

public interface IHopitalService {
    Facture saveFacture(Facture facture);

    Medecin saveMedecin(Medecin medecin);

    RendezVous saveRendezVous(RendezVous rendezVous);

    Consultation saveConsultation(Consultation consultation);
}

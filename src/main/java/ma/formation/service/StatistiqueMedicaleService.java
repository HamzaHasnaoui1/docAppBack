// src/main/java/ma/formation/service/StatistiqueMedicaleService.java
package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.DonneesPhysiologiques;
import ma.formation.entities.Patient;
import ma.formation.repositories.DonneesPhysiologiquesRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.utils.MedicalCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatistiqueMedicaleService {
    private final DonneesPhysiologiquesRepository donneesPhysiologiquesRepository;
    private final PatientRepository patientRepository;

    /**
     * Génère un rapport complet des tendances de santé d'un patient
     */
    @Transactional(readOnly = true)
    public Map<String, Object> genererRapportSante(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        // Récupérer tous les relevés physiologiques du patient, triés par date
        List<DonneesPhysiologiques> historiqueDonnees = donneesPhysiologiquesRepository
                .findLatestByPatientId(patientId);

        Map<String, Object> rapport = new HashMap<>();
        rapport.put("patientId", patientId);
        rapport.put("patientNom", patient.getNom());
        rapport.put("dateRapport", new Date());

        // Données démographiques
        Map<String, Object> demographiques = new HashMap<>();
        demographiques.put("age", calculerAge((java.sql.Date) patient.getDateNaissance()));
        demographiques.put("groupeSanguin",
                patient.getDossierMedical() != null ? patient.getDossierMedical().getGroupeSanguin() : "Non spécifié");
        rapport.put("demographiques", demographiques);

        // Si aucune donnée n'est disponible
        if (historiqueDonnees.isEmpty()) {
            rapport.put("message", "Aucune donnée physiologique disponible pour ce patient");
            return rapport;
        }

        // Récupérer la dernière mesure
        DonneesPhysiologiques derniereMesure = historiqueDonnees.get(0);
        Map<String, Object> etatActuel = new HashMap<>();

        if (derniereMesure.getPoids() != null)
            etatActuel.put("poids", derniereMesure.getPoids());

        if (derniereMesure.getTaille() != null)
            etatActuel.put("taille", derniereMesure.getTaille());

        if (derniereMesure.getImc() != null) {
            double imc = derniereMesure.getImc();
            etatActuel.put("imc", imc);
            etatActuel.put("categorieIMC", MedicalCalculator.getIMCCategory(imc));
        }

        if (derniereMesure.getTensionSystolique() != null && derniereMesure.getTensionDiastolique() != null) {
            Map<String, Object> tension = new HashMap<>();
            tension.put("systolique", derniereMesure.getTensionSystolique());
            tension.put("diastolique", derniereMesure.getTensionDiastolique());
            tension.put("statut", MedicalCalculator.getBloodPressureStatus(
                    derniereMesure.getTensionSystolique(), derniereMesure.getTensionDiastolique()));
            etatActuel.put("tension", tension);
        }

        rapport.put("etatActuel", etatActuel);

        // Analyser les tendances
        if (historiqueDonnees.size() > 1) {
            Map<String, Object> tendances = new HashMap<>();

            // Tendance du poids
            List<Double> poidsList = historiqueDonnees.stream()
                    .filter(d -> d.getPoids() != null)
                    .map(DonneesPhysiologiques::getPoids)
                    .collect(Collectors.toList());

            if (poidsList.size() >= 2) {
                double variationPoids = poidsList.get(0) - poidsList.get(poidsList.size() - 1);
                tendances.put("variationPoids", Math.round(variationPoids * 10.0) / 10.0);
                tendances.put("tendancePoids", variationPoids > 0 ? "Augmentation" :
                        variationPoids < 0 ? "Diminution" : "Stable");
            }

            // Tendance de l'IMC
            List<Double> imcList = historiqueDonnees.stream()
                    .filter(d -> d.getImc() != null)
                    .map(DonneesPhysiologiques::getImc)
                    .collect(Collectors.toList());

            if (imcList.size() >= 2) {
                double variationIMC = imcList.get(0) - imcList.get(imcList.size() - 1);
                tendances.put("variationIMC", Math.round(variationIMC * 10.0) / 10.0);
                tendances.put("tendanceIMC", variationIMC > 0 ? "Augmentation" :
                        variationIMC < 0 ? "Diminution" : "Stable");
            }

            // Tendance de la tension
            List<DonneesPhysiologiques> tensionList = historiqueDonnees.stream()
                    .filter(d -> d.getTensionSystolique() != null && d.getTensionDiastolique() != null)
                    .collect(Collectors.toList());

            if (tensionList.size() >= 2) {
                DonneesPhysiologiques premiere = tensionList.get(tensionList.size() - 1);
                DonneesPhysiologiques derniere = tensionList.get(0);

                int variationSystolique = derniere.getTensionSystolique() - premiere.getTensionSystolique();
                int variationDiastolique = derniere.getTensionDiastolique() - premiere.getTensionDiastolique();

                Map<String, Object> tendanceTension = new HashMap<>();
                tendanceTension.put("variationSystolique", variationSystolique);
                tendanceTension.put("variationDiastolique", variationDiastolique);
                tendanceTension.put("tendanceSystolique", variationSystolique > 0 ? "Augmentation" :
                        variationSystolique < 0 ? "Diminution" : "Stable");
                tendanceTension.put("tendanceDiastolique", variationDiastolique > 0 ? "Augmentation" :
                        variationDiastolique < 0 ? "Diminution" : "Stable");

                tendances.put("tension", tendanceTension);
            }

            rapport.put("tendances", tendances);
        }

        // Générer des recommandations basiques
        List<String> recommandations = new ArrayList<>();

        if (derniereMesure.getImc() != null) {
            double imc = derniereMesure.getImc();
            if (imc < 18.5) {
                recommandations.add("IMC bas - Consultation nutritionnelle recommandée");
            } else if (imc >= 25 && imc < 30) {
                recommandations.add("Surpoids - Suivi régulier du poids recommandé");
            } else if (imc >= 30) {
                recommandations.add("Obésité - Consultation spécialisée recommandée");
            }
        }

        if (derniereMesure.getTensionSystolique() != null && derniereMesure.getTensionDiastolique() != null) {
            int sys = derniereMesure.getTensionSystolique();
            int dia = derniereMesure.getTensionDiastolique();

            if (sys >= 140 || dia >= 90) {
                recommandations.add("Tension artérielle élevée - Suivi régulier recommandé");
            }
            if (sys >= 160 || dia >= 100) {
                recommandations.add("Hypertension - Consultation spécialisée recommandée");
            }
        }

        rapport.put("recommandations", recommandations);

        return rapport;
    }

    /**
     * Calcule l'âge d'une personne à partir de sa date de naissance
     */
    private int calculerAge(java.sql.Date dateNaissance) {
        if (dateNaissance == null) return 0;
        return Period.between(dateNaissance.toLocalDate(), LocalDate.now()).getYears();
    }
}
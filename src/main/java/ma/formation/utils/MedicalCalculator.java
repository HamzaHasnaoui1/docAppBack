// src/main/java/ma/formation/utils/MedicalCalculator.java
package ma.formation.utils;

import ma.formation.entities.DonneesPhysiologiques;

public class MedicalCalculator {

    /**
     * Calcule l'Indice de Masse Corporelle (IMC)
     * Formule : poids (kg) / (taille (m))²
     *
     * @param poids en kilogrammes
     * @param taille en centimètres
     * @return l'IMC arrondi à une décimale
     */
    public static double calculateIMC(double poids, double taille) {
        if (taille <= 0) return 0;

        double tailleEnMetres = taille / 100.0;
        double imc = poids / (tailleEnMetres * tailleEnMetres);

        // Arrondir à une décimale
        return Math.round(imc * 10.0) / 10.0;
    }

    /**
     * Détermine la catégorie d'IMC selon les standards de l'OMS
     *
     * @param imc L'indice de masse corporelle
     * @return La catégorie d'IMC sous forme de chaîne
     */
    public static String getIMCCategory(double imc) {
        if (imc < 16.5) return "Dénutrition sévère";
        if (imc < 18.5) return "Maigreur";
        if (imc < 25.0) return "Poids normal";
        if (imc < 30.0) return "Surpoids";
        if (imc < 35.0) return "Obésité modérée (Classe 1)";
        if (imc < 40.0) return "Obésité sévère (Classe 2)";
        return "Obésité morbide (Classe 3)";
    }

    /**
     * Détermine si une tension artérielle est normale, élevée, ou indique une hypertension
     *
     * @param systolique Tension systolique (premier chiffre)
     * @param diastolique Tension diastolique (deuxième chiffre)
     * @return Le statut de la tension artérielle
     */
    public static String getBloodPressureStatus(int systolique, int diastolique) {
        if (systolique < 120 && diastolique < 80) return "Normale";
        if (systolique < 130 && diastolique < 85) return "Normale élevée";
        if (systolique < 140 && diastolique < 90) return "Élevée";
        if (systolique < 160 && diastolique < 100) return "Hypertension légère (grade 1)";
        if (systolique < 180 && diastolique < 110) return "Hypertension modérée (grade 2)";
        return "Hypertension sévère (grade 3)";
    }

    /**
     * Calcule automatiquement l'IMC dans un objet DonneesPhysiologiques
     * si le poids et la taille sont disponibles
     *
     * @param donnees Objet contenant les données physiologiques
     */
    public static void calculateAndSetIMC(DonneesPhysiologiques donnees) {
        if (donnees.getPoids() != null && donnees.getTaille() != null && donnees.getTaille() > 0) {
            double imc = calculateIMC(donnees.getPoids(), donnees.getTaille());
            donnees.setImc(imc);
        }
    }

    /**
     * Calcule l'écart par rapport à la norme pour un paramètre
     *
     * @param valeur Valeur mesurée
     * @param min Minimum de la plage normale
     * @param max Maximum de la plage normale
     * @return Pourcentage d'écart (positif si au-dessus, négatif si en-dessous)
     */
    public static double calculateDeviation(double valeur, double min, double max) {
        double moyenne = (min + max) / 2;
        double ecart = valeur - moyenne;
        return Math.round((ecart / moyenne) * 1000.0) / 10.0; // pourcentage avec 1 décimale
    }

    /**
     * Calcule les besoins caloriques quotidiens de base (formule de Harris-Benedict)
     *
     * @param poids en kilogrammes
     * @param taille en centimètres
     * @param age en années
     * @param estHomme true si homme, false si femme
     * @return Besoins caloriques quotidiens en kcal
     */
    public static double calculateBasalMetabolicRate(double poids, double taille, int age, boolean estHomme) {
        if (estHomme) {
            return 88.362 + (13.397 * poids) + (4.799 * taille) - (5.677 * age);
        } else {
            return 447.593 + (9.247 * poids) + (3.098 * taille) - (4.330 * age);
        }
    }

    /**
     * Calcule l'âge à partir de la date de naissance
     *
     * @param dateNaissance Date de naissance
     * @return Âge en années
     */
    public static int calculateAge(java.util.Date dateNaissance) {
        if (dateNaissance == null) return 0;

        java.time.LocalDate birthDate = dateNaissance.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
        java.time.LocalDate currentDate = java.time.LocalDate.now();

        return currentDate.getYear() - birthDate.getYear() -
                (currentDate.getDayOfYear() < birthDate.getDayOfYear() ? 1 : 0);
    }
}
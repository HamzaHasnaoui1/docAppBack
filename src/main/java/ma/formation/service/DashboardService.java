package ma.formation.service;

import lombok.RequiredArgsConstructor;
import ma.formation.entities.Medecin;
import ma.formation.entities.Patient;
import ma.formation.entities.RendezVous;
import ma.formation.enums.StatusRDV;
import ma.formation.repositories.MedecinRepository;
import ma.formation.repositories.OrdonnanceRepository;
import ma.formation.repositories.PatientRepository;
import ma.formation.repositories.RendezVousRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service fournissant les données pour le tableau de bord de l'application.
 * Inclut des statistiques sur les patients, rendez-vous, médecins et activité globale du cabinet.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final MedecinRepository medecinRepository;

    /**
     * Récupère toutes les statistiques pour le tableau de bord principal
     * @return Map contenant les différentes catégories de statistiques
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> dashboard = new HashMap<>();

        // Récupérer les périodes standards pour les statistiques
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // Ajouter les statistiques au dashboard
        dashboard.put("summary", getSummaryStatistics());
        dashboard.put("patientStatistics", getPatientStatistics());
        dashboard.put("appointmentStatistics", getAppointmentStatistics(startOfMonth, endOfMonth));
        dashboard.put("medecinStatistics", getMedecinStatistics());
        dashboard.put("revenueStatistics", getRevenueStatistics(startOfMonth, endOfMonth));
        dashboard.put("weeklyAppointments", getWeeklyAppointments(startOfWeek, endOfWeek));
        dashboard.put("trendData", getTrendData(today.minusMonths(5), today));

        return dashboard;
    }

    /**
     * Statistiques résumées pour les widgets principaux du tableau de bord
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSummaryStatistics() {
        Map<String, Object> summary = new HashMap<>();

        // Dates pour filtrage
        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrowDate = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Compter les patients, rendez-vous et médecins
        long totalPatients = patientRepository.count();
        long totalAppointments = rendezVousRepository.count();
        long totalMedecins = medecinRepository.count();

        // Patients malades
        List<Patient> patients = patientRepository.findAll();
        long sickPatients = patients.stream().filter(Patient::isMalade).count();

        // Rendez-vous aujourd'hui
        List<RendezVous> todayAppointments = rendezVousRepository.findByDate(todayDate);

        // Rendez-vous en attente
        long pendingAppointments = rendezVousRepository.findByStatus(StatusRDV.PENDING).size() +
                rendezVousRepository.findByStatus(StatusRDV.PENDING).size();

        summary.put("totalPatients", totalPatients);
        summary.put("totalAppointments", totalAppointments);
        summary.put("totalMedecins", totalMedecins);
        summary.put("sickPatients", sickPatients);
        summary.put("sickPatientsPercentage", totalPatients > 0 ? (double) sickPatients / totalPatients * 100 : 0);
        summary.put("todayAppointments", todayAppointments.size());
        summary.put("pendingAppointments", pendingAppointments);

        return summary;
    }

    /**
     * Statistiques détaillées sur les patients
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPatientStatistics() {
        Map<String, Object> patientStats = new HashMap<>();

        List<Patient> allPatients = patientRepository.findAll();

        // Distribution par âge
        Map<String, Long> ageDistribution = new HashMap<>();
        ageDistribution.put("0-18", 0L);
        ageDistribution.put("19-35", 0L);
        ageDistribution.put("36-50", 0L);
        ageDistribution.put("51-65", 0L);
        ageDistribution.put("65+", 0L);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        for (Patient patient : allPatients) {
            if (patient.getDateNaissance() != null) {
                calendar.setTime(patient.getDateNaissance());
                int birthYear = calendar.get(Calendar.YEAR);
                int age = currentYear - birthYear;

                if (age <= 18) {
                    ageDistribution.put("0-18", ageDistribution.get("0-18") + 1);
                } else if (age <= 35) {
                    ageDistribution.put("19-35", ageDistribution.get("19-35") + 1);
                } else if (age <= 50) {
                    ageDistribution.put("36-50", ageDistribution.get("36-50") + 1);
                } else if (age <= 65) {
                    ageDistribution.put("51-65", ageDistribution.get("51-65") + 1);
                } else {
                    ageDistribution.put("65+", ageDistribution.get("65+") + 1);
                }
            }
        }

        // Patients par sexe (basé sur le titre)
        long malePatients = allPatients.stream()
                .filter(p -> p.getTitre() != null && p.getTitre().equals("Mr"))
                .count();
        long femalePatients = allPatients.stream()
                .filter(p -> p.getTitre() != null && p.getTitre().equals("MMe"))
                .count();

        Map<String, Long> genderDistribution = new HashMap<>();
        genderDistribution.put("Hommes", malePatients);
        genderDistribution.put("Femmes", femalePatients);

        // Patients avec des dossiers médicaux complets
        long patientsWithCompleteMedicalRecords = allPatients.stream()
                .filter(p -> p.getDossierMedical() != null &&
                        !isNullOrEmpty(p.getDossierMedical().getAllergies()) &&
                        !isNullOrEmpty(p.getDossierMedical().getAntecedents()))
                .count();

        // Top 5 des patients avec le plus de rendez-vous
        List<Map<String, Object>> topPatientsByAppointments = allPatients.stream()
                .map(patient -> {
                    int appointmentCount = patient.getRendezVousList() != null ?
                            patient.getRendezVousList().size() : 0;

                    Map<String, Object> patientData = new HashMap<>();
                    patientData.put("id", patient.getId());
                    patientData.put("nom", patient.getNom());
                    patientData.put("appointmentCount", appointmentCount);
                    return patientData;
                })
                .sorted(Comparator.comparing(p -> ((Integer) p.get("appointmentCount")), Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toList());

        patientStats.put("ageDistribution", ageDistribution);
        patientStats.put("genderDistribution", genderDistribution);
        patientStats.put("patientsWithCompleteMedicalRecords", patientsWithCompleteMedicalRecords);
        patientStats.put("medicalRecordsCompletionRate",
                allPatients.size() > 0 ? (double) patientsWithCompleteMedicalRecords / allPatients.size() * 100 : 0);
        patientStats.put("topPatientsByAppointments", topPatientsByAppointments);

        // Nouveaux patients par mois (6 derniers mois)
        Map<String, Long> newPatientsByMonth = getNewPatientsLastMonths(6);
        patientStats.put("newPatientsByMonth", newPatientsByMonth);

        return patientStats;
    }

    /**
     * Statistiques des rendez-vous pour une période donnée
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointmentStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> appointmentStats = new HashMap<>();

        // Convertir LocalDate en Date pour les requêtes
        Date startDateAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Récupérer les rendez-vous dans la période
        List<RendezVous> rdvs = rendezVousRepository.findByDateBetween(startDateAsDate, endDateAsDate);

        // Rendez-vous par statut
        Map<String, Long> appointmentsByStatus = rdvs.stream()
                .collect(Collectors.groupingBy(
                        rdv -> rdv.getStatusRDV().name(),
                        Collectors.counting()));

        // Taux de complétion et d'annulation
        long totalRdvs = rdvs.size();
        long completedRdvs = rdvs.stream()
                .filter(rdv -> rdv.getStatusRDV() == StatusRDV.DONE )
                .count();
        long cancelledRdvs = rdvs.stream()
                .filter(rdv -> rdv.getStatusRDV() == StatusRDV.CANCELLED )
                .count();

        double completionRate = totalRdvs > 0 ? (double) completedRdvs / totalRdvs * 100 : 0;
        double cancellationRate = totalRdvs > 0 ? (double) cancelledRdvs / totalRdvs * 100 : 0;

        // Rendez-vous par jour de la semaine
        Map<String, Long> appointmentsByDayOfWeek = rdvs.stream()
                .collect(Collectors.groupingBy(
                        rdv -> {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(rdv.getDate());
                            return getDayOfWeekString(cal.get(Calendar.DAY_OF_WEEK));
                        },
                        Collectors.counting()));

        // Durée moyenne d'attente entre la prise de rendez-vous et le rendez-vous
        // Note: Ceci est une simulation car nous n'avons pas de champ pour la date de prise de rendez-vous
        double averageWaitingDays = 5.3; // Valeur simulée

        appointmentStats.put("totalAppointments", totalRdvs);
        appointmentStats.put("appointmentsByStatus", appointmentsByStatus);
        appointmentStats.put("completionRate", completionRate);
        appointmentStats.put("cancellationRate", cancellationRate);
        appointmentStats.put("appointmentsByDayOfWeek", appointmentsByDayOfWeek);
        appointmentStats.put("averageWaitingDays", averageWaitingDays);

        return appointmentStats;
    }

    /**
     * Statistiques sur les médecins
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMedecinStatistics() {
        Map<String, Object> medecinStats = new HashMap<>();

        List<Medecin> allMedecins = medecinRepository.findAll();

        // Nombre de médecins par spécialité
        Map<String, Long> medecinsBySpeciality = allMedecins.stream()
                .collect(Collectors.groupingBy(
                        medecin -> medecin.getSpecialite() != null ? medecin.getSpecialite() : "Non spécifié",
                        Collectors.counting()));

        // Charge de travail par médecin (nombre de rendez-vous)
        List<Map<String, Object>> medecinWorkload = new ArrayList<>();

        for (Medecin medecin : allMedecins) {
            Map<String, Object> workloadData = new HashMap<>();
            List<RendezVous> medecinRdvs = rendezVousRepository.findByMedecin_Id(medecin.getId());

            workloadData.put("id", medecin.getId());
            workloadData.put("nom", medecin.getNom());
            workloadData.put("specialite", medecin.getSpecialite());
            workloadData.put("totalAppointments", medecinRdvs.size());

            // Calcul du taux de complétion des rendez-vous par médecin
            long completedRdvs = medecinRdvs.stream()
                    .filter(rdv -> rdv.getStatusRDV() == StatusRDV.DONE )
                    .count();

            double completionRate = medecinRdvs.size() > 0 ?
                    (double) completedRdvs / medecinRdvs.size() * 100 : 0;

            workloadData.put("completionRate", completionRate);

            medecinWorkload.add(workloadData);
        }

        // Trier par nombre de rendez-vous décroissant
        medecinWorkload.sort((m1, m2) ->
                ((Integer) m2.get("totalAppointments")).compareTo((Integer) m1.get("totalAppointments")));

        medecinStats.put("totalMedecins", allMedecins.size());
        medecinStats.put("medecinsBySpeciality", medecinsBySpeciality);
        medecinStats.put("medecinWorkload", medecinWorkload);

        return medecinStats;
    }

    /**
     * Statistiques financières pour une période donnée
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRevenueStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> revenueStats = new HashMap<>();

        // Convertir LocalDate en Date pour les requêtes
        Date startDateAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Récupérer les rendez-vous dans la période
        List<RendezVous> rdvs = rendezVousRepository.findByDateBetween(startDateAsDate, endDateAsDate);

        // Calculer le revenu total
        double totalRevenue = rdvs.stream()
                .mapToDouble(RendezVous::getPrix)
                .sum();

        // Calculer le revenu moyen par rendez-vous
        double averageRevenue = rdvs.isEmpty() ? 0 : totalRevenue / rdvs.size();

        // Revenu par mois (pour les 6 derniers mois)
        Map<String, Double> revenueByMonth = getRevenueByMonth(6);

        // Revenu par spécialité médicale
        Map<String, Double> revenueBySpeciality = new HashMap<>();

        for (RendezVous rdv : rdvs) {
            if (rdv.getMedecin() != null) {
                String specialite = rdv.getMedecin().getSpecialite() != null ?
                        rdv.getMedecin().getSpecialite() : "Non spécifié";

                revenueBySpeciality.put(
                        specialite,
                        revenueBySpeciality.getOrDefault(specialite, 0.0) + rdv.getPrix());
            }
        }

        // Projections de revenus pour le mois suivant (simulation)
        double projectedRevenue = totalRevenue * 1.05; // +5% de croissance simulée

        revenueStats.put("totalRevenue", totalRevenue);
        revenueStats.put("averageRevenue", averageRevenue);
        revenueStats.put("revenueByMonth", revenueByMonth);
        revenueStats.put("revenueBySpeciality", revenueBySpeciality);
        revenueStats.put("projectedRevenue", projectedRevenue);

        return revenueStats;
    }

    /**
     * Récupère les rendez-vous pour une semaine donnée, organisés par jour
     */
    @Transactional(readOnly = true)
    public Map<String, List<Map<String, Object>>> getWeeklyAppointments(LocalDate startOfWeek, LocalDate endOfWeek) {
        Map<String, List<Map<String, Object>>> weeklyAppointments = new HashMap<>();

        // Initialiser les jours de la semaine
        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        for (String day : daysOfWeek) {
            weeklyAppointments.put(day, new ArrayList<>());
        }

        // Convertir LocalDate en Date pour les requêtes
        Date startDateAsDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Récupérer les rendez-vous de la semaine
        List<RendezVous> weekRdvs = rendezVousRepository.findByDateBetween(startDateAsDate, endDateAsDate);

        // Organiser les rendez-vous par jour
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        for (RendezVous rdv : weekRdvs) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(rdv.getDate());
            String dayOfWeek = getDayOfWeekString(cal.get(Calendar.DAY_OF_WEEK));

            Map<String, Object> rdvData = new HashMap<>();
            rdvData.put("id", rdv.getId());
            rdvData.put("time", sdf.format(rdv.getDate()));
            rdvData.put("patientId", rdv.getPatient().getId());
            rdvData.put("patientName", rdv.getPatient().getNom());
            rdvData.put("medecinId", rdv.getMedecin().getId());
            rdvData.put("medecinName", rdv.getMedecin().getNom());
            rdvData.put("status", rdv.getStatusRDV().name());

            weeklyAppointments.get(dayOfWeek).add(rdvData);
        }

        // Trier les rendez-vous par heure pour chaque jour
        for (String day : daysOfWeek) {
            weeklyAppointments.get(day).sort((rdv1, rdv2) ->
                    ((String) rdv1.get("time")).compareTo((String) rdv2.get("time")));
        }

        return weeklyAppointments;
    }

    /**
     * Calcule les données de tendance sur plusieurs mois
     */
    @Transactional(readOnly = true)
    public Map<String, List<Object>> getTrendData(LocalDate startDate, LocalDate endDate) {
        Map<String, List<Object>> trendData = new HashMap<>();
        List<Object> months = new ArrayList<>();
        List<Object> patients = new ArrayList<>();
        List<Object> appointments = new ArrayList<>();
        List<Object> revenue = new ArrayList<>();

        LocalDate current = startDate;
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        while (!current.isAfter(endDate)) {
            LocalDate monthStart = current.withDayOfMonth(1);
            LocalDate monthEnd = current.withDayOfMonth(current.lengthOfMonth());

            // Convertir en Date pour les requêtes
            Date monthStartDate = Date.from(monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date monthEndDate = Date.from(monthEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Compter les nouveaux patients pour ce mois (simulation)
            // Dans un cas réel, on utiliserait une requête sur la date de création
            long newPatients = (long) (Math.random() * 20 + 5); // Simulé: entre 5 et 25 nouveaux patients

            // Compter les rendez-vous pour ce mois
            List<RendezVous> monthRdvs = rendezVousRepository.findByDateBetween(monthStartDate, monthEndDate);

            // Calculer le revenu pour ce mois
            double monthRevenue = monthRdvs.stream()
                    .mapToDouble(RendezVous::getPrix)
                    .sum();

            // Ajouter aux données de tendance
            months.add(current.format(monthFormatter));
            patients.add(newPatients);
            appointments.add(monthRdvs.size());
            revenue.add(monthRevenue);

            // Passer au mois suivant
            current = current.plusMonths(1);
        }

        trendData.put("months", months);
        trendData.put("newPatients", patients);
        trendData.put("appointments", appointments);
        trendData.put("revenue", revenue);

        return trendData;
    }

    /**
     * Obtenir les nouveaux patients sur les derniers mois
     */
    private Map<String, Long> getNewPatientsLastMonths(int numberOfMonths) {
        Map<String, Long> newPatientsByMonth = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        // Simulation de données pour les nouveaux patients par mois
        // Dans un vrai système, on utiliserait une requête sur la date de création
        for (int i = numberOfMonths - 1; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            String monthYear = monthDate.format(monthFormatter);

            // Simulation: entre 5 et 25 nouveaux patients par mois
            long newPatients = (long) (Math.random() * 20 + 5);
            newPatientsByMonth.put(monthYear, newPatients);
        }

        return newPatientsByMonth;
    }

    /**
     * Obtenir les revenus par mois
     */
    private Map<String, Double> getRevenueByMonth(int numberOfMonths) {
        Map<String, Double> revenueByMonth = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (int i = numberOfMonths - 1; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            LocalDate monthStart = monthDate.withDayOfMonth(1);
            LocalDate monthEnd = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
            String monthYear = monthDate.format(monthFormatter);

            // Convertir en Date pour les requêtes
            Date monthStartDate = Date.from(monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date monthEndDate = Date.from(monthEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Récupérer les rendez-vous du mois
            List<RendezVous> monthRdvs = rendezVousRepository.findByDateBetween(monthStartDate, monthEndDate);

            // Calculer le revenu du mois
            double monthRevenue = monthRdvs.stream()
                    .mapToDouble(RendezVous::getPrix)
                    .sum();

            revenueByMonth.put(monthYear, monthRevenue);
        }

        return revenueByMonth;
    }

    /**
     * Convertit un numéro de jour de semaine en chaîne
     */
    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY: return "Lundi";
            case Calendar.TUESDAY: return "Mardi";
            case Calendar.WEDNESDAY: return "Mercredi";
            case Calendar.THURSDAY: return "Jeudi";
            case Calendar.FRIDAY: return "Vendredi";
            case Calendar.SATURDAY: return "Samedi";
            case Calendar.SUNDAY: return "Dimanche";
            default: return "Inconnu";
        }
    }

    /**
     * Vérifie si une chaîne est nulle ou vide
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
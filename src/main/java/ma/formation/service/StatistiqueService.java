//package ma.formation.service;
//
//import lombok.RequiredArgsConstructor;
//import ma.formation.entities.Consultation;
//import ma.formation.entities.Patient;
//import ma.formation.entities.Paiement;
//import ma.formation.entities.RendezVous;
//import ma.formation.enums.StatusRDV;
//import ma.formation.repositories.ConsultationRepository;
//import ma.formation.repositories.PaiementRepository;
//import ma.formation.repositories.PatientRepository;
//import ma.formation.repositories.RendezVousRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class StatistiqueService {
//    private final ConsultationRepository consultationRepository;
//    private final RendezVousRepository rendezVousRepository;
//    private final PaiementRepository paiementRepository;
//    private final PatientRepository patientRepository;
//
//    /**
//     * Calcule le chiffre d'affaires pour une période donnée
//     */
//    @Transactional(readOnly = true)
//    public Map<String, Object> getRevenueStatistics(LocalDate startDate, LocalDate endDate) {
//        List<Paiement> paiements = paiementRepository.findAll();
//
//        // Filtrer par période
//        List<Paiement> filteredPaiements = paiements.stream()
//                .filter(p -> !p.getDatePaiement().isBefore(startDate) && !p.getDatePaiement().isAfter(endDate))
//                .collect(Collectors.toList());
//
//        // Calculer le CA total
//        double totalRevenue = filteredPaiements.stream()
//                .mapToDouble(Paiement::getMontant)
//                .sum();
//
//        // Calculer le CA moyen par consultation
//        double averageRevenue = filteredPaiements.isEmpty() ? 0 :
//                totalRevenue / filteredPaiements.size();
//
//        // Grouper par mode de paiement
//        Map<String, Double> revenueByPaymentMethod = filteredPaiements.stream()
//                .collect(Collectors.groupingBy(
//                        p -> p.getModePaiement().name(),
//                        Collectors.summingDouble(Paiement::getMontant)));
//
//        // Grouper par mois si la période est plus longue qu'un mois
//        Map<String, Double> revenueByMonth = new LinkedHashMap<>();
//        if (startDate.getMonthValue() != endDate.getMonthValue() ||
//                startDate.getYear() != endDate.getYear()) {
//
//            filteredPaiements.stream()
//                    .collect(Collectors.groupingBy(
//                            p -> p.getDatePaiement().getYear() + "-" + p.getDatePaiement().getMonthValue(),
//                            Collectors.summingDouble(Paiement::getMontant)))
//                    .entrySet().stream()
//                    .sorted(Map.Entry.comparingByKey())
//                    .forEach(e -> revenueByMonth.put(e.getKey(), e.getValue()));
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("totalRevenue", totalRevenue);
//        result.put("averageRevenue", averageRevenue);
//        result.put("revenueByPaymentMethod", revenueByPaymentMethod);
//        result.put("revenueByMonth", revenueByMonth);
//        result.put("numberOfConsultations", filteredPaiements.size());
//
//        return result;
//    }
//
//    /**
//     * Calcule les statistiques des rendez-vous
//     */
//    @Transactional(readOnly = true)
//    public Map<String, Object> getAppointmentStatistics(LocalDate startDate, LocalDate endDate) {
//        List<RendezVous> allRdvs = rendezVousRepository.findAll();
//
//        // Convertir LocalDate en Date pour la comparaison
//        Date startDateAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        Date endDateAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        // Filtrer les RDV pour la période
//        List<RendezVous> filteredRdvs = allRdvs.stream()
//                .filter(rdv -> !rdv.getDate().before(startDateAsDate) && !rdv.getDate().after(endDateAsDate))
//                .collect(Collectors.toList());
//
//        // Calculer le nombre de RDV par statut
//        Map<String, Long> rdvByStatus = filteredRdvs.stream()
//                .collect(Collectors.groupingBy(
//                        rdv -> rdv.getStatusRDV().name(),
//                        Collectors.counting()));
//
//        // Calculer le taux de RDV honorés vs annulés
//        long totalRdvs = filteredRdvs.size();
//        long completedRdvs = filteredRdvs.stream()
//                .filter(rdv -> rdv.getStatusRDV() == StatusRDV.DONE || rdv.getStatusRDV() == StatusRDV.TERMINE)
//                .count();
//        long cancelledRdvs = filteredRdvs.stream()
//                .filter(rdv -> rdv.getStatusRDV() == StatusRDV.CANCELED || rdv.getStatusRDV() == StatusRDV.ANNULE)
//                .count();
//
//        double completionRate = totalRdvs > 0 ? (double) completedRdvs / totalRdvs * 100 : 0;
//        double cancellationRate = totalRdvs > 0 ? (double) cancelledRdvs / totalRdvs * 100 : 0;
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("totalAppointments", totalRdvs);
//        result.put("appointmentsByStatus", rdvByStatus);
//        result.put("completionRate", completionRate);
//        result.put("cancellationRate", cancellationRate);
//
//        return result;
//    }
//
//    /**
//     * Calcule les statistiques des patients
//     */
//    @Transactional(readOnly = true)
//    public Map<String, Object> getPatientStatistics() {
//        List<Patient> allPatients = patientRepository.findAll();
//
//        // Nombre total de patients
//        long totalPatients = allPatients.size();
//
//        // Nombre de patients par état de santé (malade/non malade)
//        long sickPatients = allPatients.stream().filter(Patient::isMalade).count();
//        long healthyPatients = totalPatients - sickPatients;
//
//        // Top 5 des patients avec le plus de consultations
//        List<Map<String, Object>> topPatientsByConsultations = allPatients.stream()
//                .map(patient -> {
//                    int consultationCount = patient.getRendezVousList().stream()
//                            .filter(rdv -> rdv.getConsultation() != null)
//                            .collect(Collectors.toList()).size();
//
//                    Map<String, Object> patientData = new HashMap<>();
//                    patientData.put("id", patient.getId());
//                    patientData.put("nom", patient.getNom());
//                    patientData.put("consultationCount", consultationCount);
//                    return patientData;
//                })
//                .sorted(Comparator.comparing(p -> ((Integer) p.get("consultationCount")), Comparator.reverseOrder()))
//                .limit(5)
//                .collect(Collectors.toList());
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("totalPatients", totalPatients);
//        result.put("sickPatients", sickPatients);
//        result.put("healthyPatients", healthyPatients);
//        result.put("sickPercentage", totalPatients > 0 ? (double) sickPatients / totalPatients * 100 : 0);
//        result.put("topPatientsByConsultations", topPatientsByConsultations);
//
//        return result;
//    }
//
//    /**
//     * Calcule les statistiques des consultations
//     */
//    @Transactional(readOnly = true)
//    public Map<String, Object> getConsultationStatistics(LocalDate startDate, LocalDate endDate) {
//        List<Consultation> allConsultations = consultationRepository.findAll();
//
//        // Convertir LocalDate en Date pour la comparaison
//        Date startDateAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        Date endDateAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        // Filtrer les consultations pour la période
//        List<Consultation> filteredConsultations = allConsultations.stream()
//                .filter(c -> !c.getDateConsultation().before(startDateAsDate) && !c.getDateConsultation().after(endDateAsDate))
//                .collect(Collectors.toList());
//
//        // Nombre total de consultations dans la période
//        long totalConsultations = filteredConsultations.size();
//
//        // Prix moyen des consultations
//        double averagePrice = filteredConsultations.stream()
//                .mapToDouble(c -> {
//                    try {
//                        return c.getPrix();
//                    } catch (NumberFormatException | NullPointerException e) {
//                        return 0.0;
//                    }
//                })
//                .average()
//                .orElse(0.0);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("totalConsultations", totalConsultations);
//        result.put("averagePrice", averagePrice);
//
//        return result;
//    }
//}
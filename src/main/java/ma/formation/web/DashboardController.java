package ma.formation.web;

import lombok.RequiredArgsConstructor;
import ma.formation.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Contrôleur pour fournir les données du tableau de bord et des statistiques
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    /**
     * Endpoint principal pour récupérer toutes les statistiques du dashboard
     */
    @Secured({"ROLE_ADMIN"})
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        return ResponseEntity.ok(dashboardService.getDashboardStatistics());
    }

    /**
     * Endpoint pour les statistiques résumées (widgets principaux)
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummaryStatistics() {
        return ResponseEntity.ok(dashboardService.getSummaryStatistics());
    }

    /**
     * Endpoint pour les statistiques des patients
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/patients")
    public ResponseEntity<Map<String, Object>> getPatientStatistics() {
        return ResponseEntity.ok(dashboardService.getPatientStatistics());
    }

    /**
     * Endpoint pour les statistiques des rendez-vous
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/appointments")
    public ResponseEntity<Map<String, Object>> getAppointmentStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Si les dates ne sont pas spécifiées, utiliser le mois en cours
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        return ResponseEntity.ok(dashboardService.getAppointmentStatistics(startDate, endDate));
    }

    /**
     * Endpoint pour les statistiques des médecins
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/medecins")
    public ResponseEntity<Map<String, Object>> getMedecinStatistics() {
        return ResponseEntity.ok(dashboardService.getMedecinStatistics());
    }

    /**
     * Endpoint pour les statistiques financières
     */
    @Secured({"ROLE_ADMIN"})
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Si les dates ne sont pas spécifiées, utiliser le mois en cours
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        return ResponseEntity.ok(dashboardService.getRevenueStatistics(startDate, endDate));
    }

    /**
     * Endpoint pour récupérer les rendez-vous de la semaine en cours
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/weekly-appointments")
    public ResponseEntity<Map<String, Object>> getWeeklyAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {

        // Si la date de début n'est pas spécifiée, utiliser le lundi de la semaine en cours
        if (weekStart == null) {
            weekStart = LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        }

        LocalDate weekEnd = weekStart.plusDays(6);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("weekStart", weekStart);
        response.put("weekEnd", weekEnd);
        response.put("appointments", dashboardService.getWeeklyAppointments(weekStart, weekEnd));

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour récupérer les données de tendance
     */
    @Secured({"ROLE_ADMIN"})
    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrendData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Si les dates ne sont pas spécifiées, utiliser les 6 derniers mois
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("trends", dashboardService.getTrendData(startDate, endDate));

        return ResponseEntity.ok(response);
    }
}
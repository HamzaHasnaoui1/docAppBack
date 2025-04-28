//package ma.formation.web;
//
//import lombok.RequiredArgsConstructor;
//import ma.formation.service.StatistiqueService;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/stats")
//@CrossOrigin("*")
//@RequiredArgsConstructor
//public class StatistiqueController {
//    private final StatistiqueService statisticalService;
//
//    @Secured({"ROLE_ADMIN"})
//    @GetMapping("/revenue")
//    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        return ResponseEntity.ok(statisticalService.getRevenueStatistics(startDate, endDate));
//    }
//
//    @Secured({"ROLE_ADMIN"})
//    @GetMapping("/appointments")
//    public ResponseEntity<Map<String, Object>> getAppointmentStatistics(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        return ResponseEntity.ok(statisticalService.getAppointmentStatistics(startDate, endDate));
//    }
//
//    @Secured({"ROLE_ADMIN"})
//    @GetMapping("/patients")
//    public ResponseEntity<Map<String, Object>> getPatientStatistics() {
//        return ResponseEntity.ok(statisticalService.getPatientStatistics());
//    }
//
//    @Secured({"ROLE_ADMIN"})
//    @GetMapping("/consultations")
//    public ResponseEntity<Map<String, Object>> getConsultationStatistics(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        return ResponseEntity.ok(statisticalService.getConsultationStatistics(startDate, endDate));
//    }
//
//    @Secured({"ROLE_ADMIN"})
//    @GetMapping("/dashboard")
//    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
//        // Pour le tableau de bord, utiliser la période des 30 derniers jours par défaut
//        LocalDate endDate = LocalDate.now();
//        LocalDate startDate = endDate.minusDays(30);
//
//        Map<String, Object> dashboardStats = new HashMap<>();
//
//        // Récupérer toutes les statistiques clés
//        dashboardStats.put("revenue", statisticalService.getRevenueStatistics(startDate, endDate));
//        dashboardStats.put("appointments", statisticalService.getAppointmentStatistics(startDate, endDate));
//        dashboardStats.put("patients", statisticalService.getPatientStatistics());
//        dashboardStats.put("consultations", statisticalService.getConsultationStatistics(startDate, endDate));
//
//        return ResponseEntity.ok(dashboardStats);
//    }
//}
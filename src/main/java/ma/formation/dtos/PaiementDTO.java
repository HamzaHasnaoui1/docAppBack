package ma.formation.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaiementDTO {
    private Long id;
    private Double montant;
    private LocalDate datePaiement;
    private String modePaiement;
    private Long patientId;
    private Long consultationId;
}

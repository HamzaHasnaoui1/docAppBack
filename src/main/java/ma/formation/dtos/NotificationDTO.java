package ma.formation.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private String message;
    private boolean lu;
    private LocalDateTime dateEnvoi;
    private Long patientId;
}
package ma.formation.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrdonnanceDTO {
    private Long id;
    private String contenu;
    private LocalDate dateEmission;
}
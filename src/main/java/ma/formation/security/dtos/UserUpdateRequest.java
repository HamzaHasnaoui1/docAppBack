package ma.formation.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserUpdateRequest {
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    private String phoneNumber;

    private boolean active;

    @NotEmpty
    private Set<String> roles;

    @Size(min = 6, max = 20)
    private String password; // Optionnel

}
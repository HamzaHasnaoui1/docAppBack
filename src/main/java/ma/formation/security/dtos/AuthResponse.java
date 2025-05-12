package ma.formation.security.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AuthResponse {
    private String userId;
    private String username;
    private String email;
    private String token;
    private List<String> roles;
}
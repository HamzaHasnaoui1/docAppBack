package ma.formation.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean active;
    private List<String> roles;
    private List<String> permissions;
}
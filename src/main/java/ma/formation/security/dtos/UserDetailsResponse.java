package ma.formation.security.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailsResponse {
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean active;
    private List<String> roles;
}
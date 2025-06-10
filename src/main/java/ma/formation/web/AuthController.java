package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.security.entities.AppUser;
import ma.formation.security.entities.AppRole;
import ma.formation.security.dtos.AuthRequest;
import ma.formation.security.dtos.AuthResponse;
import ma.formation.security.dtos.RegisterRequest;
import ma.formation.security.repositories.AppUserRepository;
import ma.formation.security.repositories.AppRoleRepository;
import ma.formation.security.service.JwtService;
import ma.formation.exceptions.BusinessException;
import ma.formation.exceptions.DuplicateResourceException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            AppUser user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            String token = jwtService.generateToken(user);

            // Create response with user details and token
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRoles(getRoleNames(user));
            if (user.getMedecin() != null) {
                response.setMedecinId(user.getMedecin().getId());
            }

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            throw new BusinessException("Nom d'utilisateur ou mot de passe invalide");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Get USER role
        AppRole userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new BusinessException("Role 'USER' not found"));

        // Create new user
        AppUser newUser = new AppUser();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setNumeroTelephone(request.getPhoneNumber());
        newUser.setActive(true);
        newUser.setAppRoles(new ArrayList<>(Collections.singletonList(userRole)));

        AppUser savedUser = userRepository.save(newUser);

        // Generate token for the new user
        String token = jwtService.generateToken(savedUser);

        // Create response
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(savedUser.getUserId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setRoles(getRoleNames(savedUser));
        if (savedUser.getMedecin() != null) {
            response.setMedecinId(savedUser.getMedecin().getId());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT is stateless, so we don't need to do anything server-side
        // The client should discard the token
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-token")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = jwtService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/user-info")
    public ResponseEntity<AuthResponse> getUserInfo(@RequestParam String token) {
        if (!jwtService.validateToken(token)) {
            throw new BusinessException("Invalid or expired token");
        }

        String username = jwtService.extractUsername(token);
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));

        AuthResponse response = new AuthResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoles(getRoleNames(user));
        if (user.getMedecin() != null) {
            response.setMedecinId(user.getMedecin().getId());
        }

        return ResponseEntity.ok(response);
    }

    private List<String> getRoleNames(AppUser user) {
        List<String> roleNames = new ArrayList<>();
        if (user.getAppRoles() != null) {
            user.getAppRoles().forEach(role -> roleNames.add(role.getRoleName()));
        }
        return roleNames;
    }
}
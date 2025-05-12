package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.security.dtos.UserCreationRequest;
import ma.formation.security.dtos.UserDetailsResponse;
import ma.formation.security.dtos.UserUpdateRequest;
import ma.formation.security.entities.AppRole;
import ma.formation.security.entities.AppUser;
import ma.formation.security.repositories.AppRoleRepository;
import ma.formation.security.repositories.AppUserRepository;
import ma.formation.exceptions.BusinessException;
import ma.formation.exceptions.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminAuthController {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Secured("ROLE_ADMIN")
    @PostMapping("/create-user")
    public ResponseEntity<UserDetailsResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        // Create roles list
        List<AppRole> roles = new ArrayList<>();
        for (String roleName : request.getRoles()) {
            AppRole role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
            roles.add(role);
        }

        // Create new user
        AppUser newUser = new AppUser();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setNumeroTelephone(request.getPhoneNumber());
        newUser.setActive(request.isActive());
        newUser.setAppRoles(roles);

        AppUser savedUser = userRepository.save(newUser);

        // Create response
        UserDetailsResponse response = new UserDetailsResponse();
        response.setUserId(savedUser.getUserId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setActive(savedUser.isActive());
        response.setPhoneNumber(savedUser.getNumeroTelephone());
        response.setRoles(savedUser.getAppRoles().stream()
                .map(AppRole::getRoleName)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserDetailsResponse> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequest request) {

        AppUser existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!existingUser.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (!existingUser.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }


        List<AppRole> roles = new ArrayList<>();
        for (String roleName : request.getRoles()) {
            AppRole role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
            roles.add(role);
        }

        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setNumeroTelephone(request.getPhoneNumber());
        existingUser.setActive(request.isActive());
        existingUser.setAppRoles(roles);

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        AppUser updatedUser = userRepository.save(existingUser);

        UserDetailsResponse response = new UserDetailsResponse();
        response.setUserId(updatedUser.getUserId());
        response.setUsername(updatedUser.getUsername());
        response.setEmail(updatedUser.getEmail());
        response.setActive(updatedUser.isActive());
        response.setPhoneNumber(updatedUser.getNumeroTelephone());
        response.setRoles(updatedUser.getAppRoles().stream()
                .map(AppRole::getRoleName)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public ResponseEntity<List<UserDetailsResponse>> getAllUsers() {
        List<AppUser> users = userRepository.findAll();

        List<UserDetailsResponse> response = users.stream()
                .map(user -> {
                    UserDetailsResponse dto = new UserDetailsResponse();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setActive(user.isActive());
                    dto.setPhoneNumber(user.getNumeroTelephone());
                    dto.setRoles(user.getAppRoles().stream()
                            .map(AppRole::getRoleName)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable String userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserDetailsResponse response = new UserDetailsResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setActive(user.isActive());
        response.setPhoneNumber(user.getNumeroTelephone());
        response.setRoles(user.getAppRoles().stream()
                .map(AppRole::getRoleName)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<UserDetailsResponse> activateUser(@PathVariable String userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setActive(true);
        AppUser savedUser = userRepository.save(user);

        UserDetailsResponse response = new UserDetailsResponse();
        response.setUserId(savedUser.getUserId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setActive(savedUser.isActive());
        response.setPhoneNumber(savedUser.getNumeroTelephone());
        response.setRoles(savedUser.getAppRoles().stream()
                .map(AppRole::getRoleName)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<UserDetailsResponse> deactivateUser(@PathVariable String userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setActive(false);
        AppUser savedUser = userRepository.save(user);

        UserDetailsResponse response = new UserDetailsResponse();
        response.setUserId(savedUser.getUserId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setActive(savedUser.isActive());
        response.setPhoneNumber(savedUser.getNumeroTelephone());
        response.setRoles(savedUser.getAppRoles().stream()
                .map(AppRole::getRoleName)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/users/{userId}/change-role")
    public ResponseEntity<UserDetailsResponse> changeUserRole(
            @PathVariable String userId,
            @RequestBody List<String> roleNames) {

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<AppRole> roles = new ArrayList<>();
        for (String roleName : roleNames) {
            AppRole role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
            roles.add(role);
        }

        user.setAppRoles(roles);
        AppUser savedUser = userRepository.save(user);

        UserDetailsResponse response = new UserDetailsResponse();
        response.setUserId(savedUser.getUserId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setActive(savedUser.isActive());
        response.setPhoneNumber(savedUser.getNumeroTelephone());
        response.setRoles(savedUser.getAppRoles().stream()
                .map(AppRole::getRoleName)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/users/{userId}/reset-password")
    public ResponseEntity<Void> resetUserPassword(
            @PathVariable String userId,
            @RequestBody Map<String, String> passwordRequest) {

        String newPassword = passwordRequest.get("password");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BusinessException("Password cannot be empty");
        }

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
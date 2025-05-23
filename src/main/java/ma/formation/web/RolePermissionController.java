package ma.formation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.formation.security.annotations.RequirePermission;
import ma.formation.security.dtos.PermissionDTO;
import ma.formation.security.dtos.RoleDTO;
import ma.formation.security.service.PermissionService;
import ma.formation.security.service.RolePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;

    // ===== Endpoints pour les rôles =====
    
    @GetMapping("/roles")
    @RequirePermission("ROLE_LIST")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(rolePermissionService.getAllRoles());
    }
    
    @GetMapping("/roles/{id}")
    @RequirePermission("ROLE_VIEW")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(rolePermissionService.getRoleById(id));
    }
    
    @GetMapping("/roles/name/{roleName}")
    @RequirePermission("ROLE_VIEW")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String roleName) {
        return ResponseEntity.ok(rolePermissionService.getRoleByName(roleName));
    }
    
    @PostMapping("/roles")
    @RequirePermission("ROLE_CREATE")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        return new ResponseEntity<>(rolePermissionService.createRole(roleDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/roles/{id}")
    @RequirePermission("ROLE_UPDATE")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(rolePermissionService.updateRole(id, roleDTO));
    }
    
    @DeleteMapping("/roles/{id}")
    @RequirePermission("ROLE_DELETE")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        rolePermissionService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
    
    // ===== Endpoints pour associer/dissocier des permissions aux rôles =====
    
    @PostMapping("/roles/{roleId}/permissions/{permissionId}")
    @RequirePermission("ROLE_PERMISSION_ASSIGN")
    public ResponseEntity<RoleDTO> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return ResponseEntity.ok(rolePermissionService.addPermissionToRole(roleId, permissionId));
    }
    
    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    @RequirePermission("ROLE_PERMISSION_REMOVE")
    public ResponseEntity<RoleDTO> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return ResponseEntity.ok(rolePermissionService.removePermissionFromRole(roleId, permissionId));
    }
    
    // ===== Endpoints pour les permissions =====
    
    @GetMapping("/permissions")
    @RequirePermission("PERMISSION_LIST")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
    
    @GetMapping("/permissions/category/{category}")
    @RequirePermission("PERMISSION_LIST")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(permissionService.getPermissionsByCategory(category));
    }
    
    @GetMapping("/permissions/{id}")
    @RequirePermission("PERMISSION_VIEW")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }
    
    @PostMapping("/permissions")
    @RequirePermission("PERMISSION_CREATE")
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        return new ResponseEntity<>(permissionService.createPermission(permissionDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/permissions/{id}")
    @RequirePermission("PERMISSION_UPDATE")
    public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(permissionService.updatePermission(id, permissionDTO));
    }
    
    @DeleteMapping("/permissions/{id}")
    @RequirePermission("PERMISSION_DELETE")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
} 
package ma.formation.security.service;

import lombok.RequiredArgsConstructor;
import ma.formation.exceptions.ResourceNotFoundException;
import ma.formation.security.dtos.PermissionDTO;
import ma.formation.security.dtos.RoleDTO;
import ma.formation.security.entities.AppRole;
import ma.formation.security.entities.Permission;
import ma.formation.security.repositories.AppRoleRepository;
import ma.formation.security.repositories.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RolePermissionService {
    
    private final AppRoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public RoleDTO getRoleById(Long id) {
        AppRole role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return mapToDTO(role);
    }
    
    public RoleDTO getRoleByName(String roleName) {
        AppRole role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));
        return mapToDTO(role);
    }
    
    public RoleDTO createRole(RoleDTO roleDTO) {
        AppRole role = new AppRole();
        role.setRoleName(roleDTO.getRoleName());
        role.setDescription(roleDTO.getDescription());
        
        if (roleDTO.getPermissions() != null) {
            List<Permission> permissions = roleDTO.getPermissions().stream()
                    .map(dto -> permissionRepository.findById(dto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + dto.getId())))
                    .collect(Collectors.toList());
            role.setPermissions(permissions);
        }
        
        AppRole savedRole = roleRepository.save(role);
        return mapToDTO(savedRole);
    }
    
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        AppRole role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        
        role.setRoleName(roleDTO.getRoleName());
        role.setDescription(roleDTO.getDescription());
        
        if (roleDTO.getPermissions() != null) {
            List<Permission> permissions = roleDTO.getPermissions().stream()
                    .map(dto -> permissionRepository.findById(dto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + dto.getId())))
                    .collect(Collectors.toList());
            role.setPermissions(permissions);
        }
        
        AppRole updatedRole = roleRepository.save(role);
        return mapToDTO(updatedRole);
    }
    
    public RoleDTO addPermissionToRole(Long roleId, Long permissionId) {
        AppRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));
        
        if (!role.getPermissions().contains(permission)) {
            role.getPermissions().add(permission);
            roleRepository.save(role);
        }
        
        return mapToDTO(role);
    }
    
    public RoleDTO removePermissionFromRole(Long roleId, Long permissionId) {
        AppRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));
        
        role.getPermissions().remove(permission);
        roleRepository.save(role);
        
        return mapToDTO(role);
    }
    
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
    
    private RoleDTO mapToDTO(AppRole role) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getRoleId());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        
        if (role.getPermissions() != null) {
            dto.setPermissions(role.getPermissions().stream()
                    .map(this::mapToPermissionDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private PermissionDTO mapToPermissionDTO(Permission permission) {
        return new PermissionDTO(
                permission.getId(),
                permission.getName(),
                permission.getDescription(),
                permission.getCategory()
        );
    }
} 
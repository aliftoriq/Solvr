package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.dto.role.FeatureResponse;
import id.co.bcaf.solvr.dto.role.RoleResponse;
import id.co.bcaf.solvr.model.account.Feature;
import id.co.bcaf.solvr.model.account.Role;
import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> {
                    FeatureResponse[] featureResponses = role.getRoleToFeatures().stream()
                            .map(roleToFeature -> {
                                Feature feature = roleToFeature.getFeature();
                                return new FeatureResponse(feature.getId(), feature.getName());
                            })
                            .toArray(FeatureResponse[]::new);

                    return new RoleResponse(role.getId(), role.getName(), featureResponses);
                })
                .collect(Collectors.toList());
    }

    public Role getRoleById(int id) {
        Long roleId = Long.valueOf(id);
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        return optionalRole.orElse(null);
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role updatedRole) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setName(updatedRole.getName());
                    return roleRepository.save(existingRole);
                })
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with ID: " + id);
        }
        roleRepository.deleteById(id);
    }
}

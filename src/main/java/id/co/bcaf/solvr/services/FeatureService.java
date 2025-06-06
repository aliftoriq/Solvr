package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.dto.feature.ManyRoleToFeatureRequest;
import id.co.bcaf.solvr.dto.feature.RoleToFeatureRequest;
import id.co.bcaf.solvr.dto.role.FeatureResponse;
import id.co.bcaf.solvr.model.account.Feature;
import id.co.bcaf.solvr.model.account.Role;
import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.repository.FeatureRepository;
import id.co.bcaf.solvr.repository.RoleToFeatureRepository;
import id.co.bcaf.solvr.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    @Autowired
    private RoleToFeatureRepository roleToFeatureRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleService roleService;

    public List<Feature> getAllFeatures() {
        return featureRepository.findAll();
    }

    public List<FeatureResponse> getFeature() {
        List<Feature> feature = featureRepository.findAll();

        return feature.stream().
                map(feature1 -> new FeatureResponse(feature1.getId(), feature1.getName()))
                .collect(Collectors.toList());
    }

    public Feature createFeature(Feature feature) {
        return featureRepository.save(feature);
    }

    public Feature updateFeature(UUID id, Feature feature) {
        feature.setId(id);
        return featureRepository.save(feature);
    }

    public void deleteFeature(UUID id) {
        featureRepository.deleteById(id);
    }

    public RoleToFeature createRoleToFeature(RoleToFeatureRequest roleToFeature) {
        RoleToFeature roleToFeatureRes = new RoleToFeature();

        Feature feature = featureRepository.findById(roleToFeature.getFeatureId())
                .orElseThrow(() -> new IllegalArgumentException("Feature ID not found: " + roleToFeature.getFeatureId()));

        roleToFeatureRes.setFeature(feature);
        roleToFeatureRes.setRole(roleService.getRoleById(roleToFeature.getRoleId()));

        return roleToFeatureRepository.save(roleToFeatureRes);
    }

    public void deleteRoleToFeature(UUID id) {
        roleToFeatureRepository.deleteById(id);
    }

    public void createRoleToFeatureMany(ManyRoleToFeatureRequest request) {
        Role role = roleService.getRoleById(request.getRoleId());

        request.getListFeatureId().forEach(featureId -> {
            Feature feature = featureRepository.findById(featureId)
                    .orElseThrow(() -> new IllegalArgumentException("Feature ID not found: " + featureId));

            RoleToFeature roleToFeature = new RoleToFeature();
            roleToFeature.setRole(role);
            roleToFeature.setFeature(feature);

            roleToFeatureRepository.save(roleToFeature);
        });
    }

    public void deleteRoleToFeatureManyByRoleAndFeature(int roleId, UUID featureId) {
        Role role = roleService.getRoleById(roleId);
        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + featureId));

        roleToFeatureRepository.deleteByRoleAndFeature(role, feature);
    }

    @Transactional
    public void deleteRoleToFeatureMany(ManyRoleToFeatureRequest roleToFeature) {
        Role role = roleService.getRoleById(roleToFeature.getRoleId());

        roleToFeature.getListFeatureId().forEach(featureId -> {
            Feature feature = featureRepository.findById(featureId)
                    .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + featureId));
            roleToFeatureRepository.deleteByRoleAndFeature(role, feature);
        });
    }


    public List<RoleToFeature> getAllRoleToFeature() {
        return roleToFeatureRepository.findAll();
    }

    public List<RoleToFeature> getRoleToFeatureByRole(Role role) {
        return roleToFeatureRepository.findByRole(role);
    }

}

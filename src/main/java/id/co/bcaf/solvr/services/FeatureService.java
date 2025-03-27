package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.model.account.Feature;
import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.repository.FeatureRepository;
import id.co.bcaf.solvr.repository.RoleToFeatureRepository;
import id.co.bcaf.solvr.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public List<Feature> getFeature() {
        return featureRepository.findAll();
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

    public RoleToFeature createRoleToFeature(RoleToFeature roleToFeature) {
        return roleToFeatureRepository.save(roleToFeature);
    }

    public List<RoleToFeature> getAllRoleToFeature() {
        return roleToFeatureRepository.findAll();
    }


}

package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.model.account.Branch;
import id.co.bcaf.solvr.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public List<Branch> getAllBranch() {
        return branchRepository.findAll();
    }

    public Branch getBranchById(UUID id) {
        return branchRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Branch getNearestBranch(double latitude, double longitude) {
        List<Branch> allBranch = branchRepository.findAll();

        return allBranch.stream()
                .min(Comparator.comparingDouble(branch ->
                        haversineDistance(latitude, longitude, branch.getLatitude(), branch.getLongitude())))
                .orElse(null);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public void deleteBranch(UUID id) {
        branchRepository.deleteById(id);
    }
}

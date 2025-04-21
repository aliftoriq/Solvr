package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.model.account.PlafonPackage;
import id.co.bcaf.solvr.repository.PlafonPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlafonPackageService {
    @Autowired
    private PlafonPackageRepository plafonPackageRepository;

    public PlafonPackage createPlafonPackage(PlafonPackage plafonPackage) {
        return plafonPackageRepository.save(plafonPackage);
    }

    public List<PlafonPackage> getAllPlafonPackages() {
        return plafonPackageRepository.findAll();
    }
}

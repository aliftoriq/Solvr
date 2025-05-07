package id.co.bcaf.solvr.config.startup;

import id.co.bcaf.solvr.model.account.PlafonPackage;
import id.co.bcaf.solvr.repository.PlafonPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StartupConfig implements CommandLineRunner {

    @Autowired
    private PlafonPackageRepository plafonPackageRepository;

    @Override
    public void run(String... args) throws Exception {
        if (plafonPackageRepository.count() == 0) {
            List<PlafonPackage> packages = Arrays.asList(
                    createPlafon("Bronze Package", 10_000_000L, 1, 1.5, 6),
                    createPlafon("Silver Package", 25_000_000L, 2, 1.3, 12),
                    createPlafon("Gold Package", 50_000_000L, 3, 1.1, 18),
                    createPlafon("Platinum Package", 100_000_000L, 4, 1.0, 24),
                    createPlafon("Diamond Package", 200_000_000L, 5, 0.9, 36)
            );

            plafonPackageRepository.saveAll(packages);
        }
    }

    private PlafonPackage createPlafon(String name, Long amount, Integer level, Double interestRate, Integer tenor) {
        PlafonPackage plafon = new PlafonPackage();
        plafon.setName(name);
        plafon.setAmount(amount);
        plafon.setLevel(level);
        plafon.setInterestRate(interestRate);
        plafon.setMaxTenorMonths(tenor);
        return plafon;
    }
}

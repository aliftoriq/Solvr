package id.co.bcaf.solvr.config.startup;

import id.co.bcaf.solvr.model.account.PlafonPackage;
import id.co.bcaf.solvr.model.account.Role;
import id.co.bcaf.solvr.repository.PlafonPackageRepository;
import id.co.bcaf.solvr.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class StartupConfig {
    private static final Logger logger = LoggerFactory.getLogger(StartupConfig.class);

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository, PlafonPackageRepository plafonPackageRepository) {
        return args -> {
            initRoles(roleRepository);
            initPlafonPackages(plafonPackageRepository);
        };
    }

    private void initRoles(RoleRepository roleRepository) {
        List<String> roles = List.of("SUPER_ADMIN", "BACK_OFFICE", "BRANCH_MANAGER", "MARKETING", "CUSTOMER");

        for (String roleName : roles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                logger.info("Inserted role: {}", roleName);
            }
        }
    }

    private void initPlafonPackages(PlafonPackageRepository plafonPackageRepository) {
        if (plafonPackageRepository.count() == 0) {
            List<PlafonPackage> packages = Arrays.asList(
                    createPlafon("Bronze Package", 10_000_000L, 1, 5.5, 6),
                    createPlafon("Silver Package", 25_000_000L, 2, 5.3, 12),
                    createPlafon("Gold Package", 50_000_000L, 3, 5.0, 18),
                    createPlafon("Platinum Package", 100_000_000L, 4, 4.5, 24),
                    createPlafon("Diamond Package", 200_000_000L, 5, 4.1, 36)
            );

            plafonPackageRepository.saveAll(packages);
            logger.info("Inserted default PlafonPackages");
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
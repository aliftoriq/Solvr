package id.co.bcaf.solvr.config.startup;

import id.co.bcaf.solvr.model.account.Feature;
import id.co.bcaf.solvr.model.account.PlafonPackage;
import id.co.bcaf.solvr.model.account.Role;
import id.co.bcaf.solvr.model.account.RoleToFeature;
import id.co.bcaf.solvr.repository.FeatureRepository;
import id.co.bcaf.solvr.repository.PlafonPackageRepository;
import id.co.bcaf.solvr.repository.RoleRepository;
import id.co.bcaf.solvr.repository.RoleToFeatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class StartupConfig {
    private static final Logger logger = LoggerFactory.getLogger(StartupConfig.class);

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository,
                               PlafonPackageRepository plafonPackageRepository,
                               FeatureRepository featureRepository,
                               RoleToFeatureRepository roleToFeatureRepository) {
        return args -> {
            initRoles(roleRepository);
            initPlafonPackages(plafonPackageRepository);
            initFeatures(featureRepository);
            initRoleFeatureMappings(roleRepository, featureRepository, roleToFeatureRepository);
        };
    }

    private void initRoles(RoleRepository roleRepository) {
        List<String> roles = List.of("SUPER_ADMIN", "BRANCH MANAGER", "BACK OFFICE", "MARKETING", "CUSTOMER");

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
                    createPlafon("Bronze Package", 10_000_000L, 1, 5.5, 12),
                    createPlafon("Silver Package", 25_000_000L, 2, 5.3, 12),
                    createPlafon("Gold Package", 50_000_000L, 3, 5.0, 18),
                    createPlafon("Platinum Package", 100_000_000L, 4, 4.5, 24),
                    createPlafon("Diamond Package", 200_000_000L, 5, 4.1, 36)
            );

            plafonPackageRepository.saveAll(packages);
            logger.info("Inserted default PlafonPackages");
        }
    }

    private void initFeatures(FeatureRepository featureRepository) {
        if (featureRepository.count() == 0) {
            List<String> featureNames = Arrays.asList(
                    // Authentication Features
                    "AUTH_LOGIN", "AUTH_LOGOUT", "AUTH_REGISTER", "AUTH_VERIFY",
                    "AUTH_FORGET_PASSWORD", "AUTH_RESET_PASSWORD", "AUTH_SAVE_PASSWORD",
                    "AUTH_CHANGE_PASSWORD", "AUTH_FIREBASE_LOGIN",

                    // User Management Features
                    "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",

                    // Customer Management Features
                    "CUSTOMER_CREATE", "CUSTOMER_READ", "CUSTOMER_DETAIL", "CUSTOMER_UPDATE",

                    // Employee Management Features
                    "EMPLOYEE_CREATE", "EMPLOYEE_READ", "EMPLOYEE_UPDATE", "EMPLOYEE_DETAIL",

                    // Role & Feature Management
                    "ROLE_CREATE", "ROLE_READ", "ROLE_READ_BY_ID", "ROLE_UPDATE", "ROLE_DELETE",
                    "FEATURE_CREATE", "FEATURE_READ", "ROLE_FEATURE_CREATE", "ROLE_FEATURE_CREATE_MANY",
                    "ROLE_FEATURE_READ", "ROLE_FEATURE_DELETE_MANY",

                    // Loan Application Features
                    "LOAN_CREATE", "LOAN_READ", "LOAN_DETAIL", "LOAN_CALCULATE", "LOAN_CUSTOMER_HISTORY",
                    "LOAN_MARKETING_VIEW", "LOAN_BRANCH_MANAGER_VIEW", "LOAN_BACKOFFICE_VIEW",
                    "LOAN_REVIEW", "LOAN_APPROVE", "LOAN_REJECT", "LOAN_DISBURSE",
                    "LOAN_SUMMARY", "LOAN_HISTORY", "DASHBOARD_SUMMARY",

                    // Plafon Management Features
                    "PLAFON_CREATE", "PLAFON_UPDATE", "PLAFON_DELETE", "PLAFON_READ_ALL",

                    // Branch Management Features
                    "BRANCH_CREATE", "BRANCH_READ", "BRANCH_UPDATE", "BRANCH_DELETE", "BRANCH_NEAREST",

                    // Image Management Features
                    "IMAGE_UPLOAD", "IMAGE_KTP_UPLOAD", "IMAGE_SELFIE_UPLOAD",
                    "IMAGE_PROFILE_UPLOAD", "IMAGE_PROFILE_GET",

                    // Notification Management Features
                    "NOTIFICATION_CREATE", "NOTIFICATION_SAVE", "NOTIFICATION_TEST",

                    // System Features
                    "SYSTEM_CONTROLLERS", "SYSTEM_ENDPOINTS", "SYSTEM_HOME"
            );

            for (String featureName : featureNames) {
                Feature feature = new Feature();
                feature.setName(featureName);
                featureRepository.save(feature);
            }
            logger.info("Inserted {} features", featureNames.size());
        }
    }

    private void initRoleFeatureMappings(RoleRepository roleRepository,
                                         FeatureRepository featureRepository,
                                         RoleToFeatureRepository roleToFeatureRepository) {

        if (roleToFeatureRepository.count() > 0) {
            return;
        }

        Optional<Role> superAdmin = roleRepository.findByName("SUPER_ADMIN");
        Optional<Role> branchManager = roleRepository.findByName("BRANCH MANAGER");
        Optional<Role> backOffice = roleRepository.findByName("BACK OFFICE");
        Optional<Role> marketing = roleRepository.findByName("MARKETING");
        Optional<Role> customer = roleRepository.findByName("CUSTOMER");

        if (superAdmin.isEmpty() || branchManager.isEmpty() || marketing.isEmpty() || customer.isEmpty() || backOffice.isEmpty()) {
            logger.error("Some roles not found, skipping feature mapping initialization");
            return;
        }

        assignAllFeaturesToRole(superAdmin.get(), featureRepository, roleToFeatureRepository);

        assignFeaturesToRole(branchManager.get(), Arrays.asList(
                "AUTH_LOGIN", "AUTH_LOGOUT", "AUTH_VERIFY", "AUTH_FORGET_PASSWORD", "AUTH_RESET_PASSWORD",
                "AUTH_SAVE_PASSWORD", "AUTH_CHANGE_PASSWORD",
                "CUSTOMER_READ", "CUSTOMER_DETAIL", "EMPLOYEE_READ", "EMPLOYEE_DETAIL",
                "LOAN_BRANCH_MANAGER_VIEW", "LOAN_DETAIL", "LOAN_APPROVE", "LOAN_REJECT", "LOAN_HISTORY",
                "PLAFON_READ_ALL", "BRANCH_READ", "IMAGE_UPLOAD", "IMAGE_PROFILE_UPLOAD",
                "IMAGE_PROFILE_GET", "NOTIFICATION_CREATE", "DASHBOARD_SUMMARY"
        ), featureRepository, roleToFeatureRepository);

        assignFeaturesToRole(backOffice.get(), Arrays.asList(
                "AUTH_LOGIN", "AUTH_LOGOUT", "AUTH_VERIFY", "AUTH_FORGET_PASSWORD", "AUTH_RESET_PASSWORD",
                "AUTH_SAVE_PASSWORD", "AUTH_CHANGE_PASSWORD",
                "CUSTOMER_READ", "CUSTOMER_DETAIL", "EMPLOYEE_DETAIL",
                "LOAN_BACKOFFICE_VIEW", "LOAN_DETAIL", "LOAN_DISBURSE", "LOAN_REJECT", "LOAN_HISTORY",
                "PLAFON_READ_ALL", "BRANCH_READ", "IMAGE_UPLOAD", "IMAGE_PROFILE_UPLOAD",
                "IMAGE_PROFILE_GET", "NOTIFICATION_CREATE", "DASHBOARD_SUMMARY"
        ), featureRepository, roleToFeatureRepository);

        assignFeaturesToRole(marketing.get(), Arrays.asList(
                "AUTH_LOGIN", "AUTH_LOGOUT", "AUTH_VERIFY", "AUTH_FORGET_PASSWORD", "AUTH_RESET_PASSWORD",
                "AUTH_SAVE_PASSWORD", "AUTH_CHANGE_PASSWORD",
                "CUSTOMER_READ", "CUSTOMER_DETAIL", "EMPLOYEE_READ", "EMPLOYEE_DETAIL",
                "LOAN_MARKETING_VIEW", "LOAN_DETAIL", "LOAN_REVIEW", "LOAN_HISTORY",
                "PLAFON_READ_ALL", "BRANCH_READ", "IMAGE_UPLOAD", "IMAGE_PROFILE_UPLOAD",
                "IMAGE_PROFILE_GET", "NOTIFICATION_CREATE", "DASHBOARD_SUMMARY"
        ), featureRepository, roleToFeatureRepository);

        assignFeaturesToRole(customer.get(), Arrays.asList(
                "AUTH_LOGIN", "AUTH_LOGOUT", "AUTH_REGISTER", "AUTH_VERIFY", "AUTH_FORGET_PASSWORD",
                "AUTH_RESET_PASSWORD", "AUTH_SAVE_PASSWORD", "AUTH_CHANGE_PASSWORD", "AUTH_FIREBASE_LOGIN",
                "CUSTOMER_DETAIL", "CUSTOMER_UPDATE", "CUSTOMER_CREATE",
                "LOAN_CREATE", "LOAN_READ", "LOAN_DETAIL", "LOAN_CALCULATE", "LOAN_CUSTOMER_HISTORY", "LOAN_SUMMARY",
                "PLAFON_READ_ALL", "BRANCH_READ", "BRANCH_NEAREST",
                "IMAGE_UPLOAD", "IMAGE_KTP_UPLOAD", "IMAGE_SELFIE_UPLOAD", "IMAGE_PROFILE_UPLOAD", "IMAGE_PROFILE_GET",
                "USER_READ", "USER_CREATE"
        ), featureRepository, roleToFeatureRepository);

        logger.info("Initialized role-feature mappings");
    }

    private void assignAllFeaturesToRole(Role role, FeatureRepository featureRepository,
                                         RoleToFeatureRepository roleToFeatureRepository) {
        List<Feature> allFeatures = featureRepository.findAll();
        for (Feature feature : allFeatures) {
            RoleToFeature roleToFeature = new RoleToFeature();
            roleToFeature.setRole(role);
            roleToFeature.setFeature(feature);
            roleToFeatureRepository.save(roleToFeature);
        }
    }

    private void assignFeaturesToRole(Role role, List<String> featureNames,
                                      FeatureRepository featureRepository,
                                      RoleToFeatureRepository roleToFeatureRepository) {
        for (String featureName : featureNames) {
            Optional<Feature> feature = featureRepository.findByName(featureName);
            if (feature.isPresent()) {
                RoleToFeature roleToFeature = new RoleToFeature();
                roleToFeature.setRole(role);
                roleToFeature.setFeature(feature.get());
                roleToFeatureRepository.save(roleToFeature);
            } else {
                logger.warn("Feature not found: {}", featureName);
            }
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
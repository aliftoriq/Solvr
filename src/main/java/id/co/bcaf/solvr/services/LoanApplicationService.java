package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.config.errorHandler.CustomException;
import id.co.bcaf.solvr.dto.branch.BranchResponse;
import id.co.bcaf.solvr.dto.firebase.FirebaseNotificationRequest;
import id.co.bcaf.solvr.dto.loan.*;
import id.co.bcaf.solvr.dto.plafon.PlafonPackageResponse;
import id.co.bcaf.solvr.dto.role.RoleResponse;
import id.co.bcaf.solvr.dto.user.UserCustomerResponse;
import id.co.bcaf.solvr.dto.user.UserEmployeeResponse;
import id.co.bcaf.solvr.model.account.*;
import id.co.bcaf.solvr.repository.LoanApplicationRepository;
import id.co.bcaf.solvr.repository.LoanApplicationToEmployeeRepository;
import id.co.bcaf.solvr.repository.UserEmployeeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class LoanApplicationService {
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private LoanApplicationToEmployeeRepository loanAplicationToEmployeeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEmployeeRepository userEmployeeRepository;

    @Autowired
    private UserCustomerService userCustomerService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private JavaMailSender mailSender;


    public LoanApplication createApplication(UUID userID, LoanApplicationRequest loanApplicationRequest) {
        User user = userService.getUserById(userID);

        if (user == null) {
            log.error("User dengan ID " + userID + " tidak ditemukan");
            throw new EntityNotFoundException("User dengan ID " + userID + " tidak ditemukan");
        }

        UUID userCustomerId = user.getUserCustomer().getId();

        UserCustomer userCustomer = userCustomerService.getUserCustomerById(userCustomerId);

        if (userCustomer == null) {
            log.error("UserCustomer dengan ID " + userCustomerId + " tidak ditemukan");
            throw new EntityNotFoundException("UserCustomer dengan ID " + userCustomerId + " tidak ditemukan");
        }

        if (userCustomer.getUrlKtp() == null || userCustomer.getUrlKtp().isEmpty()) {
            throw new EntityNotFoundException("UserCustomer Belum memiliki KTP dan Selfie");
        }

        LoanApplication loanApplication = new LoanApplication();

        double admin = 50000.0;
        loanApplication.setLoanAmount(loanApplicationRequest.getLoanAmount() + admin);
        loanApplication.setLoanTenor(loanApplicationRequest.getLoanTenor());
        loanApplication.setLongitude(loanApplicationRequest.getLongitude());
        loanApplication.setLatitude(loanApplicationRequest.getLatitude());

        loanApplication.setUserCustomer(userCustomer);

        List<LoanApplication> activeLoans = loanApplicationRepository
                .findByUserCustomerAndStatus(userCustomer, "LUNAS");

        List<LoanApplication> requestedLoans = loanApplicationRepository
                .findByUserCustomerAndStatus(userCustomer, "REQUEST");

        if (requestedLoans.size() > 0) {
            throw new CustomException.InvalidInputException("UserCustomer sedang memiliki permohonan pinjaman. ");
        }

        List<LoanApplication> approvedLoans = loanApplicationRepository
                .findByUserCustomerAndStatus(userCustomer, "APPROVED");

        if (approvedLoans.size() > 0) {
            throw new CustomException.InvalidInputException("UserCustomer sedang memiliki permohonan pinjaman. ");
        }

        PlafonPackage plafonPackage = userCustomer.getPlafonPackage();
        if (plafonPackage == null) {
            throw new CustomException.InvalidInputException("UserCustomer belum memiliki PlafonPackage yang valid. ");
        }

        double activeAmount = activeLoans.stream()
                .mapToDouble(LoanApplication::getLoanAmount)
                .sum();

        double newAmount = loanApplication.getLoanAmount();
        double totalAmount = activeAmount + newAmount;

        double maxAmount = plafonPackage.getAmount();

        loanApplication.setRequestedAt(LocalDateTime.now());

        if (totalAmount > maxAmount) {
            throw new IllegalStateException("Total tenor pinjaman melebihi plafon maksimum yang diizinkan. Max Ammount : " + maxAmount + " /n Total Ammount : " + totalAmount);
        }

//        LoanApplication existingApplication = loanApplicationRepository.save(loanApplication);

//        for (LoanApplicationToEmployee lae : loanApplication.getLoanApplicationToEmployees()) {
//            // Ambil UserEmployee dari DB
//            UUID userEmployeeId = lae.getUserEmployee().getId();
//            UserEmployee userEmployee = userEmployeeRepository.findById(userEmployeeId)
//                    .orElseThrow(() -> new RuntimeException("UserEmployee not found: " + userEmployeeId));
//
//            // Set ulang UserEmployee dan hubungan dengan parent LoanApplication
//            lae.setUserEmployee(userEmployee);
//            lae.setLoanApplication(loanApplication);
//        }

        // ===================== OTOMATISASI ASSIGN EMPLOYEE =====================
        Branch nearestBranch = branchService.getNearestBranch(loanApplication.getLatitude(), loanApplication.getLongitude());

        List<UserEmployee> userEmployeeList = userEmployeeRepository.findByBranchId(nearestBranch.getId());

        List<LoanApplicationToEmployee> employeeAssignments = new ArrayList<>();

        loanApplication.setStatus("REQUEST");

        double ratePerMonth = plafonPackage.getInterestRate() / 12 / 100;
        int tenor = loanApplication.getLoanTenor();
        double monthlyInstallment = (newAmount * ratePerMonth) / (1 - Math.pow(1 + ratePerMonth, -tenor));
        loanApplication.setMonthlyPayment(monthlyInstallment);

        loanApplication.setHousingStatus(userCustomer.getHousingStatus());

        List<UserEmployee> marketingList = new ArrayList<>();

        for (UserEmployee userEmployee : userEmployeeList) {
            String roleName = userEmployee.getUser().getRole().getName();

            if (roleName.equalsIgnoreCase("BRANCH MANAGER")) {
                LoanApplicationToEmployee lae = new LoanApplicationToEmployee();
                lae.setLoanApplication(loanApplication);
                lae.setUserEmployee(userEmployee);
                employeeAssignments.add(lae);
            } else if (roleName.equalsIgnoreCase("BACK OFFICE")) {
                LoanApplicationToEmployee lae = new LoanApplicationToEmployee();
                lae.setLoanApplication(loanApplication);
                lae.setUserEmployee(userEmployee);
                employeeAssignments.add(lae);
            } else if (roleName.equalsIgnoreCase("MARKETING")) {
                marketingList.add(userEmployee);
            }
        }

        // Pilih marketing dengan beban paling sedikit
        if (!marketingList.isEmpty()) {
            UserEmployee selectedMarketing = marketingList.stream()
                    .min(Comparator.comparing(m -> loanAplicationToEmployeeRepository.countByUserEmployeeId(m.getId())))
                    .orElseThrow(() -> new RuntimeException("Gagal memilih marketing"));

            LoanApplicationToEmployee laeMarketing = new LoanApplicationToEmployee();
            laeMarketing.setLoanApplication(loanApplication);
            laeMarketing.setUserEmployee(selectedMarketing);
            employeeAssignments.add(laeMarketing);
        }

        loanApplication.setLoanApplicationToEmployees(employeeAssignments);

        return loanApplicationRepository.save(loanApplication);
    }

    public List<LoanApplication> getAllApplications() {
        return loanApplicationRepository.findAll();
    }

    public LoanApplicationDetailResponse getApplicationById(UUID id) {
        LoanApplication loanApplication = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LoanApplication dengan ID " + id + " tidak ditemukan"));

        LoanApplicationDetailResponse response = new LoanApplicationDetailResponse();

        // Set basic loan information
        response.setId(loanApplication.getId());
        response.setLoanAmount(loanApplication.getLoanAmount());
        response.setLoanTenor(loanApplication.getLoanTenor());
        response.setStatus(loanApplication.getStatus());
        response.setMonthlyPayment(loanApplication.getMonthlyPayment());
        response.setHousingStatus(loanApplication.getHousingStatus());

        // Set all datetime fields
        response.setRequestedAt(loanApplication.getRequestedAt());
        response.setReviewedAt(loanApplication.getReviewedAt());
        response.setApprovedAt(loanApplication.getApprovedAt());
        response.setDisbursedAt(loanApplication.getDisbursedAt());

        // Set location information
        response.setLongitude(loanApplication.getLongitude());
        response.setLatitude(loanApplication.getLatitude());

        // Set customer information
        UserCustomer customer = loanApplication.getUserCustomer();
        UserCustomerResponse userCustomer = new UserCustomerResponse();

        userCustomer.setId(customer.getId());
        userCustomer.setName(customer.getUser().getName());
        userCustomer.setNik(customer.getNik());
        userCustomer.setAddress(customer.getAddress());
        userCustomer.setBirthDate(customer.getBirthDate());
        userCustomer.setHousingStatus(customer.getHousingStatus());
        userCustomer.setPhone(customer.getPhone());
        userCustomer.setMotherName(customer.getMotherName());
        userCustomer.setMonthlyIncome(customer.getMonthlyIncome());
        userCustomer.setTotalPaidLoan(customer.getTotalPaidLoan());
        userCustomer.setAccountNumber(customer.getAccountNumber());

        userCustomer.setUrlSelfieWithKtp(customer.getUrlSelfieWithKtp());
        userCustomer.setUrlKtp(customer.getUrlKtp());

        response.setUserCustomer(userCustomer);
        response.setName(customer.getUser().getName()); // Set name field

        // Set employee information
        List<UserEmployeeResponse> employees = new ArrayList<>();

        for (LoanApplicationToEmployee lae : loanApplication.getLoanApplicationToEmployees()) {
            UserEmployee userEmployee = lae.getUserEmployee();

            UserEmployeeResponse employee = new UserEmployeeResponse();
            employee.setId(userEmployee.getId());
            employee.setName(userEmployee.getUser().getName());
            employee.setUsername(userEmployee.getUser().getUsername()); // Missing field
            employee.setEmail(userEmployee.getEmail());
            employee.setStatus(userEmployee.getUser().getStatus());
            employee.setDeleted(userEmployee.getUser().isDeleted()); // Missing field
            employee.setNip(userEmployee.getNip());
            employee.setDepartment(userEmployee.getDepartment());

            // Set role information
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(userEmployee.getUser().getRole().getId());
            roleResponse.setName(userEmployee.getUser().getRole().getName());
            // Set permissions if needed (third parameter in constructor was null)
            employee.setRole(roleResponse);

            // Set branch information
            BranchResponse branchResponse = new BranchResponse();
            branchResponse.setId(userEmployee.getBranch().getId());
            branchResponse.setName(userEmployee.getBranch().getName());
            branchResponse.setLatitude(userEmployee.getBranch().getLatitude());
            branchResponse.setLongitude(userEmployee.getBranch().getLongitude());
            employee.setBranch(branchResponse);

            // Set notes based on role
            String role = userEmployee.getUser().getRole().getName();
            if (role.equalsIgnoreCase("MARKETING")) {
                response.setMarketingNotes(lae.getNotes());
            } else if (role.equalsIgnoreCase("BRANCH MANAGER")) {
                response.setBranchManagerNotes(lae.getNotes());
            } else if (role.equalsIgnoreCase("BACK OFFICE")) {
                response.setBackOfficeNotes(lae.getNotes());
            }

            employees.add(employee);
        }

        response.setUserEmployee(employees);

        // Add additional information for better decision making

        // 1. Plafon information
        PlafonPackage plafonPackage = customer.getPlafonPackage();
        if (plafonPackage != null) {
            PlafonPackageResponse plafonResponse = new PlafonPackageResponse();
            plafonResponse.setId(plafonPackage.getId());
            plafonResponse.setName(plafonPackage.getName());
            plafonResponse.setAmount(plafonPackage.getAmount());
            plafonResponse.setLevel(plafonPackage.getLevel());
            plafonResponse.setInterestRate(plafonPackage.getInterestRate());
            plafonResponse.setMaxTenorMonths(plafonPackage.getMaxTenorMonths());
            response.setPlafonPackage(plafonResponse);

            // Calculate remaining plafon
            List<LoanApplication> activeLoans = loanApplicationRepository
                    .findByUserCustomerAndStatusNot(customer, "LUNAS");
            double totalActiveAmount = activeLoans.stream()
                    .mapToDouble(LoanApplication::getLoanAmount)
                    .sum();
            response.setRemainingPlafon(plafonPackage.getAmount() - totalActiveAmount);
        }

        // 2. Loan history for risk assessment
        List<LoanApplication> completedLoans = loanApplicationRepository
                .findByUserCustomerAndStatus(customer, "LUNAS");
        response.setTotalCompletedLoans(completedLoans.size());

        // 3. Active loans information
        List<LoanApplication> currentActiveLoans = loanApplicationRepository
                .findByUserCustomerAndStatus(customer, "DISBURSEMENT");
        double totalMonthlyPayment = currentActiveLoans.stream()
                .mapToDouble(LoanApplication::getMonthlyPayment)
                .sum();
        response.setTotalMonthlyPayment(totalMonthlyPayment);

        // 4. Calculate debt-to-income ratio for risk assessment
        if (customer.getMonthlyIncome() != null && customer.getMonthlyIncome() > 0) {
            double debtToIncomeRatio = (totalMonthlyPayment / customer.getMonthlyIncome()) * 100;
            response.setDebtToIncomeRatio(debtToIncomeRatio);

            // Set risk level based on debt-to-income ratio
            if (debtToIncomeRatio < 30) {
                response.setRiskLevel("LOW");
            } else if (debtToIncomeRatio < 50) {
                response.setRiskLevel("MEDIUM");
            } else {
                response.setRiskLevel("HIGH");
            }
        }

        return response;
    }

    public List<LoanApplicationDetailResponse> getAllLoanApplicationHistory(UUID userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User dengan ID " + userId + " tidak ditemukan");
        }

        UserEmployee userEmployee = user.getUserEmployee();
        if (userEmployee == null) {
            throw new IllegalArgumentException("User ini bukan karyawan/employee");
        }

        UUID employeeId = userEmployee.getId();
        List<LoanApplication> requestedLoans = loanApplicationRepository.findByEmployeeId(employeeId);

        // Filter berdasarkan role
        String role = user.getRole().getName();
        List<LoanApplication> filteredLoans = requestedLoans;

        if ("MARKETING".equalsIgnoreCase(role)) {
            filteredLoans = requestedLoans.stream()
                    .filter(loan -> !"REQUEST".equalsIgnoreCase(loan.getStatus()))
                    .collect(Collectors.toList());
        } else if ("BRANCH MANAGER".equalsIgnoreCase(role)) {
            filteredLoans = requestedLoans.stream()
                    .filter(loan -> !"APPROVED".equalsIgnoreCase(loan.getStatus()) && !"REQUEST".equalsIgnoreCase(loan.getStatus()))
                    .collect(Collectors.toList());
        } else if ("BACK OFFICE".equalsIgnoreCase(role)) {
            filteredLoans = requestedLoans.stream()
                    .filter(loan -> !"DISBURSE".equalsIgnoreCase(loan.getStatus())
                            && !"APPROVED".equalsIgnoreCase(loan.getStatus())
                            && !"REQUEST".equalsIgnoreCase(loan.getStatus()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }

        // Mapping langsung ke DTO
        return filteredLoans.stream().map(loanApplication -> {
            LoanApplicationDetailResponse response = new LoanApplicationDetailResponse();

            response.setId(loanApplication.getId());
            response.setLoanAmount(loanApplication.getLoanAmount());
            response.setLoanTenor(loanApplication.getLoanTenor());
            response.setStatus(loanApplication.getStatus());
            response.setMonthlyPayment(loanApplication.getMonthlyPayment());
            response.setHousingStatus(loanApplication.getHousingStatus());

            response.setRequestedAt(loanApplication.getRequestedAt());
            response.setReviewedAt(loanApplication.getReviewedAt());
            response.setApprovedAt(loanApplication.getApprovedAt());
            response.setDisbursedAt(loanApplication.getDisbursedAt());

            response.setLongitude(loanApplication.getLongitude());
            response.setLatitude(loanApplication.getLatitude());

            UserCustomer customer = loanApplication.getUserCustomer();
            UserCustomerResponse userCustomer = new UserCustomerResponse();
            userCustomer.setId(customer.getId());
            userCustomer.setName(customer.getUser().getName());
            userCustomer.setNik(customer.getNik());
            userCustomer.setAddress(customer.getAddress());
            userCustomer.setBirthDate(customer.getBirthDate());
            userCustomer.setHousingStatus(customer.getHousingStatus());
            userCustomer.setPhone(customer.getPhone());
            userCustomer.setMotherName(customer.getMotherName());
            userCustomer.setMonthlyIncome(customer.getMonthlyIncome());
            userCustomer.setTotalPaidLoan(customer.getTotalPaidLoan());
            userCustomer.setAccountNumber(customer.getAccountNumber());
            userCustomer.setUrlSelfieWithKtp(customer.getUrlSelfieWithKtp());
            userCustomer.setUrlKtp(customer.getUrlKtp());

            response.setUserCustomer(userCustomer);
            response.setName(customer.getUser().getName());

            // Employee & Notes
            List<UserEmployeeResponse> employees = new ArrayList<>();
            for (LoanApplicationToEmployee lae : loanApplication.getLoanApplicationToEmployees()) {
                UserEmployee emp = lae.getUserEmployee();
                UserEmployeeResponse employee = new UserEmployeeResponse();
                employee.setId(emp.getId());
                employee.setName(emp.getUser().getName());
                employee.setUsername(emp.getUser().getUsername());
                employee.setEmail(emp.getEmail());
                employee.setStatus(emp.getUser().getStatus());
                employee.setDeleted(emp.getUser().isDeleted());
                employee.setNip(emp.getNip());
                employee.setDepartment(emp.getDepartment());

                RoleResponse roleResponse = new RoleResponse();
                roleResponse.setId(emp.getUser().getRole().getId());
                roleResponse.setName(emp.getUser().getRole().getName());
                employee.setRole(roleResponse);

                BranchResponse branchResponse = new BranchResponse();
                branchResponse.setId(emp.getBranch().getId());
                branchResponse.setName(emp.getBranch().getName());
                branchResponse.setLatitude(emp.getBranch().getLatitude());
                branchResponse.setLongitude(emp.getBranch().getLongitude());
                employee.setBranch(branchResponse);

                switch (emp.getUser().getRole().getName().toUpperCase()) {
                    case "MARKETING" -> response.setMarketingNotes(lae.getNotes());
                    case "BRANCH MANAGER" -> response.setBranchManagerNotes(lae.getNotes());
                    case "BACK OFFICE" -> response.setBackOfficeNotes(lae.getNotes());
                }

                employees.add(employee);
            }

            response.setUserEmployee(employees);

            // Plafon
            PlafonPackage plafonPackage = customer.getPlafonPackage();
            if (plafonPackage != null) {
                PlafonPackageResponse plafonResponse = new PlafonPackageResponse();
                plafonResponse.setId(plafonPackage.getId());
                plafonResponse.setName(plafonPackage.getName());
                plafonResponse.setAmount(plafonPackage.getAmount());
                plafonResponse.setLevel(plafonPackage.getLevel());
                plafonResponse.setInterestRate(plafonPackage.getInterestRate());
                plafonResponse.setMaxTenorMonths(plafonPackage.getMaxTenorMonths());
                response.setPlafonPackage(plafonResponse);

                List<LoanApplication> activeLoans = loanApplicationRepository.findByUserCustomerAndStatusNot(customer, "LUNAS");
                double totalActive = activeLoans.stream().mapToDouble(LoanApplication::getLoanAmount).sum();
                response.setRemainingPlafon(plafonPackage.getAmount() - totalActive);
            }

            // History
            List<LoanApplication> completedLoans = loanApplicationRepository.findByUserCustomerAndStatus(customer, "LUNAS");
            response.setTotalCompletedLoans(completedLoans.size());

            List<LoanApplication> currentActiveLoans = loanApplicationRepository.findByUserCustomerAndStatus(customer, "DISBURSEMENT");
            double totalMonthly = currentActiveLoans.stream().mapToDouble(LoanApplication::getMonthlyPayment).sum();
            response.setTotalMonthlyPayment(totalMonthly);

            if (customer.getMonthlyIncome() != null && customer.getMonthlyIncome() > 0) {
                double dti = (totalMonthly / customer.getMonthlyIncome()) * 100;
                response.setDebtToIncomeRatio(dti);
                response.setRiskLevel(dti < 30 ? "LOW" : (dti < 50 ? "MEDIUM" : "HIGH"));
            }

            return response;
        }).collect(Collectors.toList());
    }


    public List<LoanApplication> getAllCustomerHistory(UUID userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User dengan ID " + userId + " tidak ditemukan");
        }

        UserCustomer userCustomer = user.getUserCustomer();
        if (userCustomer == null) {
            throw new IllegalArgumentException("User ini bukan customer");
        }

        UUID customerID = userCustomer.getId();

        return loanApplicationRepository.findRequestedByUserCustomerId(customerID);
    }

    public List<LoanApplicationResponse> getAllCustomerByStatus(UUID userId, String status) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User dengan ID " + userId + " tidak ditemukan");
        }

        UserEmployee userEmployee = user.getUserEmployee();
        if (userEmployee == null) {
            throw new IllegalArgumentException("User ini bukan karyawan/employee");
        }

        UUID employeeId = userEmployee.getId();

        List<LoanApplication> requestedLoans = loanApplicationRepository.findByEmployeeId(employeeId);

        // Filter to keep only REQUEST status loans
        List<LoanApplication> filteredLoans = requestedLoans.stream()
                .filter(loan -> status.equals(loan.getStatus()))
                .collect(Collectors.toList());

        // Convert to LoanApplicationResponse
        List<LoanApplicationResponse> responses = new ArrayList<>();

        for (LoanApplication loan : filteredLoans) {
            LoanApplicationResponse response = new LoanApplicationResponse();

            // Set basic loan information
            response.setId(loan.getId());
            response.setLoanAmount(loan.getLoanAmount());
            response.setLoanTenor(loan.getLoanTenor());
            response.setMonthlyPayment(loan.getMonthlyPayment());
            response.setStatus(loan.getStatus());
            response.setHousingStatus(loan.getHousingStatus());
            response.setRequestedAt(loan.getRequestedAt());
            response.setReviewedAt(loan.getReviewedAt());
            response.setApprovedAt(loan.getApprovedAt());
            response.setDisbursedAt(loan.getDisbursedAt());
            response.setLongitude(loan.getLongitude());
            response.setLatitude(loan.getLatitude());

            // Set customer information
            UserCustomer customer = loan.getUserCustomer();
            UserCustomerResponse customerResponse = new UserCustomerResponse();
            customerResponse.setId(customer.getId());
            customerResponse.setName(customer.getUser().getName());
            customerResponse.setNik(customer.getNik());
            customerResponse.setAddress(customer.getAddress());
            customerResponse.setPhone(customer.getPhone());
            customerResponse.setMotherName(customer.getMotherName());
            customerResponse.setBirthDate(customer.getBirthDate());
            customerResponse.setHousingStatus(customer.getHousingStatus());
            customerResponse.setMonthlyIncome(customer.getMonthlyIncome());
            customerResponse.setTotalPaidLoan(customer.getTotalPaidLoan());
            customerResponse.setAccountNumber(customer.getAccountNumber());

            response.setUserCustomer(customerResponse);
            response.setName(customer.getUser().getName()); // Set name from customer

            // Set employee information and notes
            List<UserEmployeeResponse> employees = new ArrayList<>();

            for (LoanApplicationToEmployee lae : loan.getLoanApplicationToEmployees()) {
                UserEmployee emp = lae.getUserEmployee();

                UserEmployeeResponse empResponse = new UserEmployeeResponse();
                empResponse.setId(emp.getId());
                empResponse.setName(emp.getUser().getName());
                empResponse.setUsername(emp.getUser().getUsername());
                empResponse.setStatus(emp.getUser().getStatus());
                empResponse.setEmail(emp.getEmail());
                empResponse.setDeleted(emp.getUser().isDeleted());
                empResponse.setDepartment(emp.getDepartment());
                empResponse.setNip(emp.getNip());

                // Set role
                RoleResponse roleResponse = new RoleResponse();
                roleResponse.setId(emp.getUser().getRole().getId());
                roleResponse.setName(emp.getUser().getRole().getName());
                empResponse.setRole(roleResponse);

                // Set branch
                BranchResponse branchResponse = new BranchResponse();
                branchResponse.setId(emp.getBranch().getId());
                branchResponse.setName(emp.getBranch().getName());
                branchResponse.setLatitude(emp.getBranch().getLatitude());
                branchResponse.setLongitude(emp.getBranch().getLongitude());
                empResponse.setBranch(branchResponse);

                employees.add(empResponse);

                // Set notes based on employee role
                String role = emp.getUser().getRole().getName();
                if (role.equalsIgnoreCase("MARKETING")) {
                    response.setMarketingNotes(lae.getNotes());
                } else if (role.equalsIgnoreCase("BRANCH MANAGER")) {
                    response.setBranchManagerNotes(lae.getNotes());
                } else if (role.equalsIgnoreCase("BACK OFFICE")) {
                    response.setBackOfficeNotes(lae.getNotes());
                }
            }

            response.setUserEmployee(employees);
            responses.add(response);
        }

        return responses;
    }


    @Transactional
    public LoanApplication reviewLoanApplication(UUID userId, UUID loanAppId, String notes) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanAppId)
                .orElseThrow(() -> new EntityNotFoundException("LoanApplication dengan ID " + loanAppId + " tidak ditemukan"));

        UUID userEmployeeId = userService.getUserById(userId).getUserEmployee().getId();

        loanApplication.setStatus("REVIEWED");
        loanApplication.setReviewedAt(LocalDateTime.now());

        LoanApplicationToEmployee lae = loanApplication.getLoanApplicationToEmployees().stream()
                .filter(l -> l.getUserEmployee().getId().equals(userEmployeeId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("LAE tidak ditemukan untuk employee ID: " + userEmployeeId));


        lae.setNotes(notes);
        loanAplicationToEmployeeRepository.save(lae);

        return loanApplicationRepository.save(loanApplication);
    }

    @Transactional
    public LoanApplication approveLoanApplication(UUID userId, UUID loanAppId, String notes) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanAppId)
                .orElseThrow(() -> new EntityNotFoundException("LoanApplication dengan ID " + loanAppId + " tidak ditemukan"));

        loanApplication.setStatus("APPROVED");
        loanApplication.setApprovedAt(LocalDateTime.now());

        UUID userEmployeeId = userService.getUserById(userId).getUserEmployee().getId();

        LoanApplicationToEmployee lae = loanApplication.getLoanApplicationToEmployees().stream()
                .filter(l -> l.getUserEmployee().getId().equals(userEmployeeId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("LAE tidak ditemukan untuk employee ID: " + userEmployeeId));

        UUID customerId = loanApplication.getUserCustomer().getId();

        List<FirebaseToken> firebaseToken = firebaseService.getFirebaseTokenByCustomer(customerId);

        if (!firebaseToken.isEmpty()) {

            FirebaseNotificationRequest request = new FirebaseNotificationRequest();
            request.setTitle("Solvr");
            request.setBody("Pengajuan Kamu telah di setujui");

            for (FirebaseToken token : firebaseToken) {
                request.setToken(token.getToken());
                firebaseService.sendNotification(request);
            }

            firebaseService.saveNotification(userId, request);
        }

        sendLoanStatusEmail(loanApplication.getUserCustomer().getUser(), "APPROVED", notes);


        lae.setNotes(notes);
        loanAplicationToEmployeeRepository.save(lae);

        return loanApplicationRepository.save(loanApplication);
    }

    @Transactional
    public LoanApplication disburseLoanApplication(UUID userId, UUID loanAppId, String notes) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanAppId)
                .orElseThrow(() -> new EntityNotFoundException("LoanApplication dengan ID " + loanAppId + " tidak ditemukan"));

        loanApplication.setStatus("DISBURSEMENT");
        loanApplication.setDisbursedAt(LocalDateTime.now());

        UUID userEmployeeId = userService.getUserById(userId).getUserEmployee().getId();

        LoanApplicationToEmployee lae = loanApplication.getLoanApplicationToEmployees().stream()
                .filter(l -> l.getUserEmployee().getId().equals(userEmployeeId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("LAE tidak ditemukan untuk employee ID: " + userEmployeeId));


        lae.setNotes(notes);
        loanAplicationToEmployeeRepository.save(lae);

        UUID customerId = loanApplication.getUserCustomer().getId();

        List<FirebaseToken> firebaseToken = firebaseService.getFirebaseTokenByCustomer(customerId);

        if (!firebaseToken.isEmpty()) {
            FirebaseNotificationRequest request = new FirebaseNotificationRequest();
            request.setTitle("Solvr");
            request.setBody("Uang telah di kirim, Segera cek rekening kamu yah!");

            for (FirebaseToken token : firebaseToken) {
                request.setToken(token.getToken());
                firebaseService.sendNotification(request);
            }

            firebaseService.saveNotification(userId, request);
        }

        sendLoanStatusEmail(loanApplication.getUserCustomer().getUser(), "DISBURSEMENT", notes);


        return loanApplicationRepository.save(loanApplication);
    }

    public void sendLoanStatusEmail(User user, String status, String notes) {
        String subject;
        String htmlContent;
        String name = user.getName();

        switch (status.toUpperCase()) {
            case "APPROVED":
                subject = "Pengajuan Kamu Disetujui - Solvr";
                htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; padding: 20px;\">" +
                        "<h2 style=\"color: #2b8a3e;\">Pengajuan Kamu Disetujui 🎉</h2>" +
                        "<p>Halo <strong>" + name + "</strong>,</p>" +
                        "<p>Selamat! Pengajuan pinjaman kamu di <strong>Solvr</strong> telah <strong>disetujui</strong>.</p>" +
                        "<p>Kami akan segera mencairkan dana ke rekening kamu setelah proses selanjutnya selesai.</p>" +
                        "<p style=\"margin-top: 20px;\">Terima kasih telah menggunakan layanan Solvr!</p>" +
                        "<hr style=\"margin-top: 30px;\">" +
                        "<p style=\"font-size: 12px; color: #999;\">Email ini dikirim secara otomatis, mohon tidak dibalas.</p>" +
                        "</div>";
                break;

            case "DISBURSEMENT":
                subject = "Dana Kamu Sudah Cair - Solvr";
                htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; padding: 20px;\">" +
                        "<h2 style=\"color: #2b8a3e;\">Dana Kamu Sudah Dikirim 💰</h2>" +
                        "<p>Halo <strong>" + name + "</strong>,</p>" +
                        "<p>Pengajuan kamu di <strong>Solvr</strong> telah berhasil dicairkan.</p>" +
                        "<p>Silakan cek rekening kamu sekarang. Dana sudah dikirimkan ke rekening yang terdaftar.</p>" +
                        "<p style=\"margin-top: 20px;\">Jika ada pertanyaan, silakan hubungi tim Solvr.</p>" +
                        "<hr style=\"margin-top: 30px;\">" +
                        "<p style=\"font-size: 12px; color: #999;\">Email ini dikirim secara otomatis, mohon tidak dibalas.</p>" +
                        "</div>";
                break;

            case "REJECTED":
                subject = "Pengajuan Kamu Ditolak - Solvr";
                htmlContent = "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; padding: 20px;\">" +
                        "<h2 style=\"color: #d9534f;\">Pengajuan Kamu Ditolak 😞</h2>" +
                        "<p>Halo <strong>" + name + "</strong>,</p>" +
                        "<p>Kami mohon maaf, pengajuan kamu di <strong>Solvr</strong> belum dapat disetujui.</p>" +
                        "<p>Catatan dari tim kami: <em>" + notes + "</em></p>" +
                        "<p>Kamu bisa mencoba kembali setelah memperbarui informasi atau menunggu beberapa waktu.</p>" +
                        "<p style=\"margin-top: 20px;\">Terima kasih telah menggunakan Solvr.</p>" +
                        "<hr style=\"margin-top: 30px;\">" +
                        "<p style=\"font-size: 12px; color: #999;\">Email ini dikirim secara otomatis, mohon tidak dibalas.</p>" +
                        "</div>";
                break;

            default:
                throw new IllegalArgumentException("Status email tidak dikenali: " + status);
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(user.getUsername());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Gagal mengirim email status pinjaman");
        }
    }


    @Transactional
    public LoanApplication rejectLoanApplication(UUID userId, UUID loanAppId, String notes) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanAppId)
                .orElseThrow(() -> new EntityNotFoundException("LoanApplication dengan ID " + loanAppId + " tidak ditemukan"));

        loanApplication.setStatus("REJECTED");

        UUID userEmployeeId = userService.getUserById(userId).getUserEmployee().getId();

        LoanApplicationToEmployee lae = loanApplication.getLoanApplicationToEmployees().stream()
                .filter(l -> l.getUserEmployee().getId().equals(userEmployeeId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("LAE tidak ditemukan untuk employee ID: " + userEmployeeId));


        // Atau langsung update:
        lae.setNotes(notes);
        loanAplicationToEmployeeRepository.save(lae);

        sendLoanStatusEmail(loanApplication.getUserCustomer().getUser(), "REJECTED", "Mohon periksa kembali data dan dokumen anda");

        return loanApplicationRepository.save(loanApplication);
    }

    public SimulationResponse calculateSimulation(UUID userId, SimulationRequest request) {

        User user = userService.getUserById(userId);
        // Cari UserCustomer berdasarkan ID
        UserCustomer userCustomer = userCustomerService.getUserCustomerById(user.getUserCustomer().getId());

        // Ambil plafon dari UserCustomer
        PlafonPackage plafon = userCustomer.getPlafonPackage();

        // Validasi apakah amount yang diminta sesuai dengan plafon yang dimiliki user
        if (request.getLoanAmount() > plafon.getAmount()) {
            throw new IllegalArgumentException("Amount exceeds available plafon");
        }

        double ratePerMonth = plafon.getInterestRate() / 12 / 100;
        double amount = request.getLoanAmount();
        int tenor = request.getLoanTenor();

        if (tenor > plafon.getMaxTenorMonths()) {
            throw new IllegalArgumentException("Tenor melebihi maksimum dari paket plafon.");
        }


        // Kalkulasi anuitas
        double monthlyInstallment = (amount * ratePerMonth) / (1 - Math.pow(1 + ratePerMonth, -tenor));
        double totalPayment = monthlyInstallment * tenor;
        double totalInterest = totalPayment - amount;

        // Admin fee: contoh tetap 50_000 seperti frontend
        double adminFee = 50000.0;

        // Response hasil simulasi
        SimulationResponse response = new SimulationResponse();
        response.setMonthlyPayment(monthlyInstallment);
        response.setTotalPayment(totalPayment);
        response.setTotalInterest(totalInterest);
        response.setAdminFee(adminFee);
        response.setRate(plafon.getInterestRate());
        response.setAmount(amount);
        response.setAmountDisbursed(amount - adminFee);
        response.setTenor(tenor);
        response.setAccountNumber(userCustomer.getAccountNumber());
        response.setAddress(userCustomer.getAddress());

        return response;
    }

    public LoanSummaryResponse getLoanApplicationSummary(UUID userId) {
        User user = userService.getUserById(userId);

        if (user.getUserCustomer() == null) {
            throw new CustomException.UserNotFoundException("User dengan ID " + userId + " tidak memiliki UserCustomer");
        }
        UserCustomer userCustomer = user.getUserCustomer();

        PlafonPackage plafon = userCustomer.getPlafonPackage();
        if (plafon.getAmount() < 0) {
            throw new IllegalArgumentException("Amount exceeds available plafon");
        }

        List<LoanApplication> loanApplications = loanApplicationRepository.findByUserCustomerAndStatus(userCustomer, "DISBURSEMENT");

        double totalLoanAmount = loanApplications.stream().mapToDouble(LoanApplication::getLoanAmount).sum();
        double monthlyInstallment = loanApplications.stream().mapToDouble(LoanApplication::getMonthlyPayment).sum();
        double remainingPlafon = plafon.getAmount() - totalLoanAmount;

        LoanSummaryResponse response = new LoanSummaryResponse();
        response.setRemainingLoan(totalLoanAmount);
        response.setName(userCustomer.getUser().getName());
        response.setAccountNumber(userCustomer.getAccountNumber());
        response.setRemainingPlafon(remainingPlafon);
        response.setMonthlyPayment(monthlyInstallment);

        // Set plafon package
        response.setPlafonPackage(new PlafonPackageResponse(
                plafon.getId(), plafon.getName(), plafon.getAmount(),
                plafon.getLevel(), plafon.getInterestRate(), plafon.getMaxTenorMonths()
        ));

        // Set userCustomer
        UserCustomerResponse customerResponse = new UserCustomerResponse();
        customerResponse.setId(userCustomer.getId());
        customerResponse.setName(userCustomer.getUser().getName());
        customerResponse.setNik(userCustomer.getNik());
        customerResponse.setAddress(userCustomer.getAddress());
        customerResponse.setBirthDate(userCustomer.getBirthDate());
        customerResponse.setHousingStatus(userCustomer.getHousingStatus());
        customerResponse.setPhone(userCustomer.getPhone());
        customerResponse.setMotherName(userCustomer.getMotherName());
        customerResponse.setMonthlyIncome(userCustomer.getMonthlyIncome());
        customerResponse.setTotalPaidLoan(userCustomer.getTotalPaidLoan());
        customerResponse.setUrlProfilePicture(userCustomer.getUser().getUrlProfilePicture());
        customerResponse.setUrlKtp(userCustomer.getUrlKtp());
        customerResponse.setUrlSelfieWithKtp(userCustomer.getUrlSelfieWithKtp());

        // Set semua pinjaman aktif
        List<LoanApplicationResponse> loanResponses = new ArrayList<>();
        for (LoanApplication loan : loanApplications) {
            LoanApplicationResponse loanResponse = new LoanApplicationResponse();
            loanResponse.setId(loan.getId());
            loanResponse.setLoanAmount(loan.getLoanAmount());
            loanResponse.setLoanTenor(loan.getLoanTenor());
            loanResponse.setStatus(loan.getStatus());
            loanResponse.setRequestedAt(loan.getRequestedAt());
            loanResponse.setApprovedAt(loan.getApprovedAt());

            loanResponse.setUserCustomer(customerResponse);

            // Set notes berdasarkan role karyawan
            for (LoanApplicationToEmployee lae : loan.getLoanApplicationToEmployees()) {
                String role = lae.getUserEmployee().getUser().getRole().getName().toUpperCase();
                String note = lae.getNotes();

                switch (role) {
                    case "MARKETING":
                        loanResponse.setMarketingNotes(note);
                        break;
                    case "BRANCH MANAGER":
                        loanResponse.setBranchManagerNotes(note);
                        break;
                    case "BACK OFFICE":
                        loanResponse.setBackOfficeNotes(note);
                        break;
                }
            }

            loanResponses.add(loanResponse);
        }

        // Cek aktif loan
        List<String> excludedStatuses = List.of("DISBURSEMENT", "REJECTED");
        List<LoanApplication> activeLoanApplications = loanApplicationRepository.findByUserCustomerAndStatusNotIn(userCustomer, excludedStatuses);

        LoanApplicationDetailResponse activeLoanApplicationResponse = new LoanApplicationDetailResponse();
        if (!activeLoanApplications.isEmpty()) {
            activeLoanApplicationResponse = getApplicationById(activeLoanApplications.getFirst().getId());
        }

        response.setActiveLoanApplication(activeLoanApplicationResponse);

        response.setActiveLoans(loanResponses);

        return response;
    }

    @Transactional
    public DashboardSummaryResponse getDashboardSummary(UUID userId) {
        User user = userService.getUserById(userId);
        String role = user.getRole().getName().toUpperCase();

        DashboardSummaryResponse response = new DashboardSummaryResponse();

        if (role.equals("SUPER_ADMIN")) {
            // Jumlah total karyawan berdasarkan role
            List<User> allEmployees = userService.findByRoleIn(List.of("MARKETING", "BRANCH MANAGER", "BACK OFFICE"));
            response.setTotalEmployees(allEmployees.size());

            // Total customer
            response.setTotalCustomers(userCustomerService.findAll().size());

            // Semua aplikasi pinjaman
            List<LoanApplication> allApplications = loanApplicationRepository.findAll();
            response.setTotalApplications(allApplications.size());

            long approvedCount = allApplications.stream()
                    .filter(app -> "APPROVED".equalsIgnoreCase(app.getStatus()))
                    .count();
            response.setApprovedApplications(approvedCount);

            List<LoanApplication> disbursed = allApplications.stream()
                    .filter(app -> "DISBURSEMENT".equalsIgnoreCase(app.getStatus()))
                    .toList();

            response.setDisbursedApplications(disbursed.size());

            double totalDisbursedAmount = disbursed.stream()
                    .mapToDouble(LoanApplication::getLoanAmount)
                    .sum();
            response.setTotalDisbursedAmount(totalDisbursedAmount);

            response.setTotalOutstandingAmount(totalDisbursedAmount); // diasumsikan semua masih outstanding

            // Estimasi interest income
            double interestIncome = disbursed.stream()
                    .mapToDouble(app -> {
                        double interestRate = app.getUserCustomer().getPlafonPackage().getInterestRate();
                        return app.getLoanAmount() * interestRate * app.getLoanTenor() / 12;
                    })
                    .sum();
            response.setTotalInterestIncome(interestIncome);
        }

        if (List.of("MARKETING", "BRANCH MANAGER", "BACK OFFICE").contains(role)) {
            List<LoanApplicationToEmployee> handledApplications =
                    loanAplicationToEmployeeRepository.findByUserEmployee_User_Id(userId);

            response.setApplicationsHandledByUser(handledApplications.size());

            long approvedByUser = handledApplications.stream()
                    .filter(lae -> "APPROVED".equalsIgnoreCase(lae.getLoanApplication().getStatus()))
                    .count();
            response.setApplicationsApprovedByUser(approvedByUser);

            long uniqueCustomers = handledApplications.stream()
                    .map(lae -> lae.getLoanApplication().getUserCustomer().getId())
                    .distinct()
                    .count();
            response.setTotalCustomers((int) uniqueCustomers);
        }

        return response;
    }


}

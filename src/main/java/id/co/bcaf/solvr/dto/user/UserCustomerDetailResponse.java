package id.co.bcaf.solvr.dto.user;

import id.co.bcaf.solvr.dto.plafon.PlafonPackageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCustomerDetailResponse {
    private UUID id;
    private String name;
    private String nik;
    private String address;
    private String phone;
    private String motherName;
    private Date birthDate;
    private String housingStatus;
    private Double monthlyIncome;
    private Long totalPaidLoan;
    private String accountNumber;
    private String urlProfilePicture;
    private String urlKtp;
    private String urlSelfieWithKtp;
    private PlafonPackageResponse plafonPackage;
}

package id.co.bcaf.solvr.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;


@Getter
@Setter
public class UserCustomerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "NIK is required")
    private String nik;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Mother's name is required")
    private String motherName;

    @NotNull(message = "Birth date is required")
    private Date birthDate;

    @NotBlank(message = "Housing status is required")
    private String housingStatus;

    @NotNull(message = "Monthly income is required")
    @Positive(message = "Monthly income must be positive")
    private Double monthlyIncome;

    @NotBlank(message = "Account number is required")
    private String accountNumber;
}

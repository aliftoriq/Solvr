package id.co.bcaf.solvr.dto.loan;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SimulationResponse {

    @NotNull(message = "Monthly payment tidak boleh kosong")
    private Double monthlyPayment;

    @NotNull(message = "Total interest tidak boleh kosong")
    private Double totalInterest;

    @NotNull(message = "Total payment tidak boleh kosong")
    private Double totalPayment;

    @NotNull(message = "Admin fee tidak boleh kosong")
    private Double adminFee;

    @NotNull(message = "Rate tidak boleh kosong")
    private Double rate;

    @NotNull(message = "Amount tidak boleh kosong")
    private Double amount;

    @NotNull(message = "Amount tidak boleh kosong")
    private Double amountDisbursed;

    @Positive(message = "Tenor harus lebih dari 0")
    private int tenor;

    @NotBlank(message = "Account number tidak boleh kosong")
    private String accountNumber;

    @NotBlank(message = "Address tidak boleh kosong")
    private String address;
}



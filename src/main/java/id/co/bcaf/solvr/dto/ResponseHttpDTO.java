package id.co.bcaf.solvr.dto;

import id.co.bcaf.solvr.model.account.Feature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ResponseHttpDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private List<String> features;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureResponse {
        private List<String> features;
    }


}

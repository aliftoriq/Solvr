package id.co.bcaf.solvr.dto;

import lombok.*;

@Getter
@Setter
public class RequestHttpDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginFirebaseRequest {
        private String token;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class ForgetPasswordReset {
        private String username;
    }

}

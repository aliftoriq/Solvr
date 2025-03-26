package id.co.bcaf.solvr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHttp {
    @Getter
    @Setter
    public class Request {
        private String username;
        private String password;

        public Request(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @Setter
    @Getter
    public static class Response {
        private String name;
        private String username;
        private String role;
        private boolean deleted = Boolean.FALSE;

        public Response(String name, String username, String role, boolean deleted) {
            this.name = name;
            this.username = username;
            this.role = role;
            this.deleted = deleted;
        }
    }
}

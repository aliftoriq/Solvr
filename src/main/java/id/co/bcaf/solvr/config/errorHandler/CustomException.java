package id.co.bcaf.solvr.config.errorHandler;

public class CustomException {

    public class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

}

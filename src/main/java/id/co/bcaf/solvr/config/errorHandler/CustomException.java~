package id.co.bcaf.solvr.config.errorHandler;

public class CustomException {

    public class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public String message;

        public InvalidInputException(String message) {
            this.message = message;
        }
    }

    public static class UserAlreadyExists extends RuntimeException {
        public UserAlreadyExists(String message) {
            super(message);
        }
    }
}

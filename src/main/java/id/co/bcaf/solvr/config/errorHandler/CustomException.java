package id.co.bcaf.solvr.config.errorHandler;

public class CustomException {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }


    public static class UserAlreadyExists extends RuntimeException {
        public UserAlreadyExists(String message) {
            super(message);
        }
    }
}

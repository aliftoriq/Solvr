package id.co.bcaf.solvr.config.errorHandler;

import id.co.bcaf.solvr.dto.ResponseTemplate;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseTemplate> handleException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(new ResponseTemplate(
                500,
                "Internal Server Error: " + ex.getMessage(),
                null
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomException.InvalidInputException.class)
    public ResponseEntity<ResponseTemplate> handleInvalidInputException(CustomException.InvalidInputException ex) {
        logger.warn("Invalid input: {}", ex.getMessage());
        return new ResponseEntity<>(new ResponseTemplate(
                400,
                "Bad Request: " + ex.getMessage(),
                null
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.UserNotFoundException.class)
    public ResponseEntity<ResponseTemplate> handleUserNotFoundException(CustomException.UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ResponseTemplate(
                404,
                "User not found: " + ex.getMessage(),
                null
        ), HttpStatus.NOT_FOUND);
    }

    // Handler untuk kesalahan validasi
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseTemplate> handleValidationException(ValidationException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        return new ResponseEntity<>(new ResponseTemplate(
                400,
                ex.getMessage(),
                null
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.UserAlreadyExists.class)
    public ResponseEntity<ResponseTemplate> handleUserAlreadyExists(CustomException.UserAlreadyExists ex) {
        logger.warn("User already exists: {}", ex.getMessage());
        return new ResponseEntity<>(new ResponseTemplate(
                409,
                "Conflict: " + ex.getMessage(),
                null
        ), HttpStatus.CONFLICT);
    }
}
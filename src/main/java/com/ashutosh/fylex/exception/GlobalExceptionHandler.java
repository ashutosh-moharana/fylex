package com.ashutosh.fylex.exception;

import com.ashutosh.fylex.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoomNotFound(RoomNotFoundException ex) {
        logger.warn("Room not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(FileNotFoundException ex) {
        logger.warn("File not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "An unexpected error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

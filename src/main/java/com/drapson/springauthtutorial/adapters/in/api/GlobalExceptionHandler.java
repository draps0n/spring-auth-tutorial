package com.drapson.springauthtutorial.adapters.in.api;

import com.drapson.springauthtutorial.application.exceptions.EmailLinkedThroughProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, DateTimeParseException.class})
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Błąd walidacji: " + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Brak dostępu");
    }

    @ExceptionHandler(EmailLinkedThroughProviderException.class)
    public ResponseEntity<String> handleEmailLinked(EmailLinkedThroughProviderException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Adres e-mail jest już powiązany z kontem przez inny dostawcę. Token łączenia: " + ex.getLinkToken());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd serwera: " + ex.getMessage());
    }
}

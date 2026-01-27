package com.example.Wallet.exception;

import com.example.Wallet.dto.TransferResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<?> handleDuplicate(DuplicateRequestException ex) {
        return ResponseEntity.ok(
                Map.of("message", ex.getMessage())
        );
    }



    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("message", ex.getMessage())
        );
    }





}

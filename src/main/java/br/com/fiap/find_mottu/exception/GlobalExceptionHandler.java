package br.com.fiap.find_mottu.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.valueOf(ex.getStatusCode().value()), //
                ex.getReason() != null ? ex.getReason() : "Erro inesperado.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, ex.getStatusCode());
    }

}

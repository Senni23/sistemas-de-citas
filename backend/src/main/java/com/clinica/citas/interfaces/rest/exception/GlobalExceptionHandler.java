package com.clinica.citas.interfaces.rest.exception;

import com.clinica.citas.domain.citas.HorarioInvalidoException;
import com.clinica.citas.domain.citas.HorarioOcupadoException;
import com.clinica.citas.domain.identidad.CredencialesInvalidasException;
import com.clinica.citas.domain.identidad.TokenInvalidoException;
import com.clinica.citas.domain.shared.DomainException;
import com.clinica.citas.domain.shared.RecursoNoEncontradoException;
import com.clinica.citas.domain.shared.RegistroDuplicadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** Convierte las excepciones del dominio en respuestas HTTP claras (404, 401, 403, 409, 400). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleRegistroDuplicado(RegistroDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(HorarioOcupadoException.class)
    public ResponseEntity<Map<String, Object>> handleHorarioOcupado(HorarioOcupadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(HorarioInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleHorarioInvalido(HorarioInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleTokenInvalido(TokenInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    /** Cualquier otra regla del dominio que se rompa (un valor invalido, un dato obligatorio...) es un error del cliente. */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDominio(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(body("No tienes permiso para realizar esta accion", HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Datos invalidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(mensaje, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body("Valor invalido: " + ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    private Map<String, Object> body(String mensaje, HttpStatus status) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now());
        map.put("status", status.value());
        map.put("mensaje", mensaje);
        return map;
    }
}

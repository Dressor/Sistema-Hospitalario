exception/GlobalExceptionHandler.java
package cl.duoc.rednorte.listaespera.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice intercepta TODAS las excepciones del proyecto
// y devuelve una respuesta JSON clara en lugar del error feo de Spring
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 1. Errores de validación (@Valid en los DTOs) ─────────────────
    //    Se dispara cuando un campo falla @NotBlank, @Email, @Size, etc.
    //    Devuelve HTTP 400 con el detalle de CADA campo inválido
    //    Ejemplo de respuesta:
    //    {
    //      "timestamp": "2025-03-29T10:00:00",
    //      "status": 400,
    //      "errores": {
    //        "rut": "El RUT es obligatorio",
    //        "email": "Formato de email inválido"
    //      }
    //    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        Map<String, String> erroresCampos = new HashMap<>();

        // Recorre cada campo que falló y guarda su mensaje de error
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresCampos.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());    // 400
        respuesta.put("error", "Validación fallida");
        respuesta.put("errores", erroresCampos);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    // ── 2. Recurso no encontrado ─────────────────────────────────────
    //    Se dispara cuando lanzamos:
    //    throw new RuntimeException("Paciente no encontrado: 5")
    //    Devuelve HTTP 404
    //    Ejemplo de respuesta:
    //    {
    //      "timestamp": "2025-03-29T10:00:00",
    //      "status": 404,
    //      "error": "Recurso no encontrado",
    //      "mensaje": "Paciente no encontrado con id: 5"
    //    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntimeException(
            RuntimeException ex) {

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.NOT_FOUND.value());      // 404
        respuesta.put("error", "Recurso no encontrado");
        respuesta.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    // ── 3. Argumento ilegal ──────────────────────────────────────────
    //    Se dispara cuando el Factory Method recibe un tipo inválido:
    //    throw new IllegalArgumentException("Tipo desconocido: WHATSAPP")
    //    Devuelve HTTP 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoIlegal(
            IllegalArgumentException ex) {

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());    // 400
        respuesta.put("error", "Argumento inválido");
        respuesta.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    // ── 4. Cualquier otro error inesperado ───────────────────────────
    //    Captura todo lo demás para no exponer el stack trace al cliente
    //    Devuelve HTTP 500
    //    Ejemplo de respuesta:
    //    {
    //      "timestamp": "2025-03-29T10:00:00",
    //      "status": 500,
    //      "error": "Error interno del servidor",
    //      "mensaje": "Contacte al equipo técnico"
    //    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGeneral(
            Exception ex) {

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        respuesta.put("error", "Error interno del servidor");
        respuesta.put("mensaje", "Contacte al equipo técnico");

        // El stack trace se imprime solo en el log del servidor,
        // nunca en la respuesta JSON al cliente (seguridad)
        ex.printStackTrace();

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(respuesta);
    }
}
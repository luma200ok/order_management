package toyproject.order.api.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    public record ErrorResponse(String code, String message) {}

    // 1) DTO 검증 실패(@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        FieldError fe = e.getBindingResult().getFieldError();
        String message = (fe != null) ? fe.getDefaultMessage() : "요청 값이 올바르지 않습니다.";
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_REQUEST", message));
    }

    // 2) 도메인 규칙 위반(파라미터/상태 이상)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_REQUEST", e.getMessage()));
    }

    // 3) 비즈니스 충돌(재고 부족 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(409).body(new ErrorResponse("CONFLICT", e.getMessage()));
    }

//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<?> handleIllegalState(IllegalStateException e) {
//        return ResponseEntity.status(409).body(e.getMessage());
//    }
}

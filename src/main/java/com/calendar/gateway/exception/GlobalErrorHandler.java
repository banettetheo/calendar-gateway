package com.calendar.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(TechnicalException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTechnicalException(TechnicalException ex) {

        var error = ex.getErrorCode();

        log.warn("⚠️ [Technical Error] Code: {} | Message: {}", error.getCode(), error.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        error.getMessage(),
                        error.getCode(),
                        LocalDateTime.now()
                )));
    }
}

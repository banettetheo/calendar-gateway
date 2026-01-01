package com.calendar.gateway.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String errorCode,
        LocalDateTime timestamp
) {
}

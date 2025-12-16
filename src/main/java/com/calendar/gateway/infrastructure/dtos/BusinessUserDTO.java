package com.calendar.gateway.infrastructure.dtos;

import java.time.LocalDateTime;

public record BusinessUserDTO(
        Long id,
        String profilePicUrl,
        LocalDateTime joinedDate
) {
}

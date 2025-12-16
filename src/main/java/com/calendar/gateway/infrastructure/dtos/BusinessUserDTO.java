package com.calendar.gateway.infrastructure.dtos;

import java.time.LocalDateTime;

public record BusinessUserDTO(
        Long id,
        String userName,
        String firstName,
        String lastName,
        String profilePicUrl,
        LocalDateTime joinedDate
) {
}

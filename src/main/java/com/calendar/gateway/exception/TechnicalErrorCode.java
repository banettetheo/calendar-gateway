package com.calendar.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechnicalErrorCode {

    REDIS_ERROR("GAT_TEC_001", "Erreur lors de la communication avec Redis"),
    USER_API_ERROR("GAT_TEC_002", "Erreur lors de la communication avec l'API des utilisateurs");

    private final String code;
    private final String message;
}

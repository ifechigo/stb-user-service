package com.suntrustbank.user.core.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("0002", "Unauthorized"),
    INTERNAL_SERVER_ERROR("0001", "An error occurred, try again!"),
    SERVICE_UNAVAILABLE("0000", "Service unavailable"),
    BAD_REQUEST("0001", "Internal server error");

    private final String code;
    private final String description;

    ErrorCode(final String code, final String description) {
        this.code = code;
        this.description = description;
    }
}

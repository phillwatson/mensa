package com.hillayes.mensa.user.auth;

import com.hillayes.mensa.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum AuthErrorCodes implements ErrorCode {
    ENCRYPTION_CONFIG("Encryption is not properly configured.");

    private final Severity severity;
    private final String message;
    private final int statusCode;

    AuthErrorCodes(Severity severity, String message, int statusCode) {
        this.severity = severity;
        this.message = message;
        this.statusCode = statusCode;
    }

    AuthErrorCodes(String message) {
        this(Severity.error, message, INTERNAL_SERVER_ERROR_STATUS);
    }

    @Override
    public String getId() {
        return name();
    }
}

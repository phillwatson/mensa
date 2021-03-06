package com.hillayes.mensa.user.auth;

import com.hillayes.mensa.exception.ErrorCode;
import com.hillayes.mensa.exception.MensaException;

public class AuthException extends MensaException {
    public AuthException(ErrorCode aErrorCode) {
        super(aErrorCode);
    }

    public AuthException(ErrorCode aErrorCode, Throwable aCause) {
        super(aErrorCode, aCause);
    }
}

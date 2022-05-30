package com.hillayes.mensa.exception.common;

import com.hillayes.mensa.exception.MensaException;

public class MissingParameterException extends MensaException {
    public MissingParameterException(String aParameterName) {
        super(CommonErrorCodes.PARAMETER_MISSING);
        addParameter("parameter-name", aParameterName);
    }
}

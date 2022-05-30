package com.hillayes.mensa.exception.common;

import com.hillayes.mensa.exception.MensaException;

public class NotFoundException extends MensaException {
    public NotFoundException(String aEntityName, Object aEntityId) {
        super(CommonErrorCodes.ENTITY_NOT_FOUND);
        addParameter("entity-name", aEntityName);
        addParameter("entity-id", aEntityId);
    }
}

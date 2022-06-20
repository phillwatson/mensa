package com.hillayes.mensa.exception.common;

import com.hillayes.mensa.exception.MensaException;

public class NotFoundException extends MensaException {
    public NotFoundException(String aEntityType, Object aEntityId) {
        super(CommonErrorCodes.ENTITY_NOT_FOUND);
        addParameter("entity-type", aEntityType);
        addParameter("entity-id", aEntityId);
    }
}

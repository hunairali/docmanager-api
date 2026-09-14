package dev.umairalishah.docmanager.exception;

import dev.umairalishah.docmanager.model.DocumentStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(DocumentStatus from, DocumentStatus to) {
        super("Cannot transition document status from " + from + " to " + to);
    }
}


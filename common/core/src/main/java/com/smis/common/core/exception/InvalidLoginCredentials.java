package com.smis.common.core.exception;

public class InvalidLoginCredentials extends DomainException {
    public InvalidLoginCredentials(String message) {
        super(message);
    }
}

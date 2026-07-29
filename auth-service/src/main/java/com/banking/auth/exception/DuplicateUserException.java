package com.banking.auth.exception;

public class DuplicateUserException extends RuntimeException {

    private final String field;

    public DuplicateUserException(String field) {
        super("An account with this " + field + " already exists");
        this.field = field;
    }

    public String getField() {
        return field;
    }
}

package com.dhanesh.auth.portal.exception;

import lombok.Getter;

@Getter
public class EmailNotVerifiedException extends RuntimeException {
    private final String email;

    public EmailNotVerifiedException(String email, String message) {
        super(message);
        this.email = email;
    }
}

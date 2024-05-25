package org.website.steez.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
        super("user not found");
    }
}

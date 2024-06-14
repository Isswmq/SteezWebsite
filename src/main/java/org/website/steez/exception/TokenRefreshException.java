package org.website.steez.exception;

public class TokenRefreshException extends RuntimeException{
    public TokenRefreshException(){
        super("token refresh exception");
    }
}

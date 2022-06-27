package com.mobile.app.ws.exceptions;

public class UserServiceException extends RuntimeException{
    private static final long serialVersionUID = 123759493203450L;
    public UserServiceException(String message) {
        super(message);
    }
}

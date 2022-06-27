package com.mobile.app.ws.exceptions;

import com.mobile.app.ws.ui.model.response.ErrorMessage;
import com.mobile.app.ws.ui.model.response.ErrorMessages;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(value = {UserServiceException.class}) // this method will handle UserServiceException when it is
    public ResponseEntity<Object> handleUserServiceException(UserServiceException ex, WebRequest request){ // thrown
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        // You can return message or whole exception
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class}) // this method will handle all others Exceptions
    public ResponseEntity<Object> handleOtherExceptions(Exception ex, WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

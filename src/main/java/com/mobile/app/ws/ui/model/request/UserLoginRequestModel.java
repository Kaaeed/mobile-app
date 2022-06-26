package com.mobile.app.ws.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestModel { // Class into which JSON payload containing email and password gets converted when request for login is sent
    private String email;
    private String password;
}

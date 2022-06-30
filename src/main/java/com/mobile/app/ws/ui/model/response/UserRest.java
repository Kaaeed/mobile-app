package com.mobile.app.ws.ui.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserRest { // Rest - response, this class contains only fields which we want to send back to the user
    private String userId; // randomly generated numeric value
    private String firstName;
    private String lastName;
    private String email;
    private List<AddressesRest> addresses;
}

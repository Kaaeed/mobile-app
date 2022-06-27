package com.mobile.app.ws.ui.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ErrorMessage {
    private Date timeStamp;
    private String message;

    public ErrorMessage(Date timeStamp, String message) {
        this.timeStamp = timeStamp;
        this.message = message;
    }
}

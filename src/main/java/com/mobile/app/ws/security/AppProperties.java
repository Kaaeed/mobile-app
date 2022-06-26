package com.mobile.app.ws.security;


import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// Class that reads from application.yml
@Component
public class AppProperties {
    @Autowired
    private Environment env;

    public String getTokenSecret(){
        return this.env.getProperty("tokenSecret");
    }
}

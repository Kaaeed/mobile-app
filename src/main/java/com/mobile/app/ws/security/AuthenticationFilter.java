package com.mobile.app.ws.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.app.ws.SpringApplicationContext;
import com.mobile.app.ws.service.UserService;
import com.mobile.app.ws.shared.dto.UserDto;
import com.mobile.app.ws.ui.model.request.UserLoginRequestModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager; // used to authenticate user

    public AuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override // this method will be used when user sends his sign in info to our server
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginRequestModel credentials = new ObjectMapper() // creates our object from JSON payload request
                    .readValue(request.getInputStream(), UserLoginRequestModel.class);

            return this.authenticationManager.authenticate( // Spring will look up user in our database if there is one
                    new UsernamePasswordAuthenticationToken( // it will trigger successfulAuthentication method
                            credentials.getEmail(), credentials.getPassword(), new ArrayList<>()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override // Spring will call this method after successful authentication
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String userName = ((User) authResult.getPrincipal()).getUsername();
        // String tokenSecret = new SecurityConstants().getTokenSecret();

        String token = Jwts.builder() // we are building the web token, it will contain username and expiration
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()) // we are signing the token with HS512
                .compact();
        // once the JWT is generated it will be included in the header and the client that receives this token
        // will need to extract this web token and will need to store it, and then every time application will need to
        // communicate with protected resources (only available to logged-in users), it will need to include this
        // JSON web token as a header into the request, otherwise the request will not be authorized
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(userName);

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        response.addHeader("UserID", userDto.getUserId());
    }
}

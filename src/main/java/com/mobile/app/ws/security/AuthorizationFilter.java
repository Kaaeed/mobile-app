package com.mobile.app.ws.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

// User authorization, it is used every time when an API call is made to protected web service endpoints
public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override // when http request is made and this filter (AuthorizationFilter) is triggered this method will be called
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        // if the header is null or doesn't start with Bearer
        if(header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);  // we continue to another filter in the chain
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    // Because we are passing request object to this function we have access to the request headers
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // We read token from authorization header
        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        if(token != null) {
            // We get rid of "Bearer" value since it is not needed, we only need JWT
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");
            // We are using JWT parser to parse the token value, decrypt it and to get the user details from that
            // web token
            String user = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret()) // If you generate JWT with one secret you have to
                    .parseClaimsJws(token)              // decrypt it with the same secret
                    .getBody()
                    .getSubject();

            if(user != null) {
                return new UsernamePasswordAuthenticationToken(user, null , new ArrayList<>());
            }

            return null;
        }

        return null;
    }
}

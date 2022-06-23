package com.mobile.app.ws.security;

import com.mobile.app.ws.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
//public class WebSecurity {
//    private UserService userService;
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
//        this.userService = userService;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        http
//                .csrf()
//                .disable()
//                .authorizeRequests(authz -> authz
//                        .antMatchers(HttpMethod.POST, "/users")
//                        .permitAll()
//                        .anyRequest()
//                        .authenticated())
//                .httpBasic(Customizer.withDefaults());
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager configureAuthentication(AuthenticationManagerBuilder auth) throws Exception{
//        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
//        return auth.build();
//    }
//
//}

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception { // to configure webservice entry points
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users") // POST at /users will all be permitted
                .permitAll()
                .anyRequest().authenticated(); // everything else will need to be authenticated
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception { // setting up authentication manager builder
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}

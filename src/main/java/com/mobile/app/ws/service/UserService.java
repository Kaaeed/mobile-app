package com.mobile.app.ws.service;

import com.mobile.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto getUser(String email);
    UserDto getUserByUserId(String userId);
    UserDto updateUser(UserDto user, String userId);
    void deleteUserById(String userId);
    List<UserDto> getUsers(int page, int limit);
}

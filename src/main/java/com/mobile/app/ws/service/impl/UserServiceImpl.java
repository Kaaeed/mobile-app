package com.mobile.app.ws.service.impl;

import com.mobile.app.ws.UserRepository;
import com.mobile.app.ws.io.entity.UserEntity;
import com.mobile.app.ws.service.UserService;
import com.mobile.app.ws.shared.Utils;
import com.mobile.app.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    UserRepository repository;
    Utils utils;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository repository, Utils utils, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.repository = repository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto user) {

        if(this.repository.findByEmail(user.getEmail()) != null) throw new RuntimeException("Record already exists");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        userEntity.setUserId(this.utils.generateUserId(30));
        userEntity.setEncryptedPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = this.repository.save(userEntity);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}

package com.mobile.app.ws.service.impl;

import com.mobile.app.ws.exceptions.UserServiceException;
import com.mobile.app.ws.io.repositories.UserRepository;
import com.mobile.app.ws.io.entity.UserEntity;
import com.mobile.app.ws.service.UserService;
import com.mobile.app.ws.shared.Utils;
import com.mobile.app.ws.shared.dto.AddressDto;
import com.mobile.app.ws.shared.dto.UserDto;
import com.mobile.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository repository;
    private Utils utils;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ModelMapper modelMapper;


    public UserServiceImpl(UserRepository repository, Utils utils,
                           BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper){
        this.repository = repository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto createUser(UserDto user) {

        if(this.repository.findByEmail(user.getEmail()) != null) throw new RuntimeException("Record already exists");

        for(int i = 0; i < user.getAddresses().size(); i++){
            AddressDto address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, address);
        }

        UserEntity userEntity =  this.modelMapper.map(user, UserEntity.class);

        userEntity.setUserId(this.utils.generateUserId(30));
        userEntity.setEncryptedPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = this.repository.save(userEntity);

        // Returns UserDto object
        return this.modelMapper.map(storedUserDetails, UserDto.class);
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = this.repository.findByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = this.repository.findByUserId(userId);

        if(userEntity == null) throw new UsernameNotFoundException("User with ID:" + userId + " not found.");

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto updateUser(UserDto user, String userId) {
        UserEntity userEntity = this.repository.findByUserId(userId);

        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUser = this.repository.save(userEntity);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(updatedUser, returnValue);
        return returnValue;
    }

    @Override
    public void deleteUserById(String userId) {
        UserEntity userEntity = this.repository.findByUserId(userId);

        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        this.repository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        if(page > 0) page--;

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = this.repository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        List<UserDto> returnValue = new ArrayList<>();

        for(UserEntity userEntity : users){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override // this method will help spring load user details when it needs, this method will be used in the process of user sign in
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = this.repository.findByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}

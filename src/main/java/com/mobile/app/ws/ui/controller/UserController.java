package com.mobile.app.ws.ui.controller;

import com.mobile.app.ws.exceptions.UserServiceException;
import com.mobile.app.ws.service.AddressService;
import com.mobile.app.ws.service.UserService;
import com.mobile.app.ws.shared.dto.AddressDto;
import com.mobile.app.ws.shared.dto.UserDto;
import com.mobile.app.ws.ui.model.request.UserDetailsRequestModel;
import com.mobile.app.ws.ui.model.response.*;
import net.bytebuddy.description.method.MethodDescription;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("users") // http://localhost:8080/mobile-app-ws/users
public class UserController {
    private UserService userService;
    private ModelMapper modelMapper;
    private AddressService addressesService;

    public UserController(UserService userService, ModelMapper modelMapper, AddressService addressesService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.addressesService = addressesService;
    }

    @GetMapping("/{id}") // (/{id}, produces= MediaType.APPLICATION_XML_VALUE) server will send responses in XML,
    public UserRest getUser(@PathVariable String id) {   // but if app requests JSON it will get an error
        UserRest returnValue = new UserRest();
        UserDto userDto = this.userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }

    // With RequestParam we are able to read request parameters from url query string
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value="page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit){
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = this.userService.getUsers(page, limit);

        for(UserDto userDto : users){
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

    @GetMapping(path = "{id}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
            )
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id){
        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDto> addressesDto = this.addressesService.getAddresses(id);

        if(addressesDto != null && !addressesDto.isEmpty()){
            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = this.modelMapper.map(addressesDto, listType);

            for(AddressesRest addressRest : returnValue){
                Link selfLink = WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                                .getUserAddress(id, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(selfLink);
            }
        }

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
                .withSelfRel();

        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @GetMapping(path = "{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
            )
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId){
        AddressDto addressDto = this.addressesService.getAddress(addressId);

        AddressesRest returnValue = this.modelMapper.map(addressDto, AddressesRest.class);

        // http://localhost:8080/users/{userId}
        // this method will produce link above
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        // http://localhost:8080/users/{userId}/addresses
        Link userAddressesLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");
        // http://localhost:8080/users/{userId}/addresses/{addressId}
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                .withSelfRel();

        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
    }

    // Same as we can specify produces, we also can specify consumes
    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        // Model mapper creates better deeper copies than BeanUtil.copyProperties
        UserDto userDto = this.modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        return this.modelMapper.map(createdUser, UserRest.class);
    }

    @PutMapping(
            path = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
            )
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(userDto, id);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();

        this.userService.deleteUserById(id);

        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }
}

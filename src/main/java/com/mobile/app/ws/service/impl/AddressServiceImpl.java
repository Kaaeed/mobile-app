package com.mobile.app.ws.service.impl;

import com.mobile.app.ws.exceptions.AddressNotFoundException;
import com.mobile.app.ws.io.entity.AddressEntity;
import com.mobile.app.ws.io.entity.UserEntity;
import com.mobile.app.ws.io.repositories.AddressRepository;
import com.mobile.app.ws.io.repositories.UserRepository;
import com.mobile.app.ws.service.AddressService;
import com.mobile.app.ws.shared.dto.AddressDto;
import com.mobile.app.ws.ui.model.response.UserRest;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private UserRepository userRepository;
    private AddressRepository addressRepository;
    private ModelMapper modelMapper;

    public AddressServiceImpl(UserRepository userRepository, AddressRepository addressRepository,
                              ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> returnValue = new ArrayList<>();

        UserEntity userEntity = this.userRepository.findByUserId(userId);

        if(userEntity == null) return returnValue;

        Iterable<AddressEntity> addresses = this.addressRepository.findAllByUserDetails(userEntity);
        for(AddressEntity addressEntity : addresses){
            returnValue.add(this.modelMapper.map(addressEntity, AddressDto.class));
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressEntity addressEntity = this.addressRepository.findByAddressId(addressId);

        if(addressEntity == null) throw new AddressNotFoundException("Address with id: " + addressId + " does not exist");

        return this.modelMapper.map(addressEntity, AddressDto.class);
    }
}

package com.mobile.app.ws.io.entity;

import com.mobile.app.ws.shared.dto.UserDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "addresses")
@Getter
@Setter
public class AddressEntity implements Serializable {
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 2359235913529L;
    @Id @GeneratedValue
    private long id;
    @Column(length = 30, nullable = false)
    private String addressId; // ID to share publicly
    @Column(length = 15, nullable = false)
    private String city;
    @Column(length = 15, nullable = false)
    private String country;
    @Column(length = 100, nullable = false)
    private String streetName;
    @Column(length = 7, nullable = false)
    private String postalCode;
    @Column(length = 10, nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;
}

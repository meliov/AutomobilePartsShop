package com.example.parts_shop_be.user.dto;

import com.example.parts_shop_be.user.UserGender;
import com.example.parts_shop_be.user.UserStatus;
import com.example.parts_shop_be.user.card_details.CardDetailsDto;
import com.example.parts_shop_be.user.role.UserRole;

import java.util.List;

public class ClientUserDto {

    private Long id;

    private String email;
    private String firstName;

    private String lastName;

    private String username;

    private String address;

    private List<UserRole> roles;
    private CardDetailsDto cardDetails;

    private UserStatus userStatus;
    public ClientUserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CardDetailsDto getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(CardDetailsDto cardDetails) {
        this.cardDetails = cardDetails;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    @Override
    public String toString() {
        return "ClientUserDto{" +
                "username='" + username + '\'' +
                '}';
    }

}

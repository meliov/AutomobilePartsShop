package com.example.parts_shop_be.user.dto;

import com.example.parts_shop_be.user.UserGender;

public class ClientUserDto {
    private Long id;

    private String email;
    private String firstName;

    private String lastName;

    private String username;

    private Double weight;

    private UserGender gender;
    private String registrationDate;

    private String updateDate;
    private CardDetailsDto cardDetailsDto;
    public CardDetailsDto getCardDetailsDto() {
        return cardDetailsDto;
    }

    public void setCardDetailsDto(CardDetailsDto cardDetailsDto) {
        this.cardDetailsDto = cardDetailsDto;
    }

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

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UserGender getGender() {
        return gender;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
    }


    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }


    @Override
    public String toString() {
        return "ClientUserDto{" +
                "username='" + username + '\'' +
                '}';
    }

}

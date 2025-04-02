package com.example.parts_shop_be.user.dto;

import com.example.parts_shop_be.user.UserGender;

public class UpdateUserDto {
    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private UserGender gender;
    private Double weight;

    private String password;
    private String profilePicture;

    public UpdateUserDto() {
    }


    public UpdateUserDto(Long id, String firstName, String lastName, String username, String password, String email, Double weightKg, String profilePicture, UserGender userGender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.weight = weightKg;
        this.profilePicture = profilePicture;
        this.gender = userGender;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weightKg) {
        this.weight = weightKg;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public UserGender getGender() {
        return gender;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

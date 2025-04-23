package com.example.parts_shop_be.user.dto;

public class CreateUserDto {
    private String password;

    private String email;
    private String username;
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public CreateUserDto(String password, String email, String username) {
        this.password = password;
        this.email = email;
        this.username = username;
    }

    public CreateUserDto() {
    }

}

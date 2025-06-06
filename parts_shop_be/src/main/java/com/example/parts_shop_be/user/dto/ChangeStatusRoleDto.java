package com.example.parts_shop_be.user.dto;

import com.example.parts_shop_be.user.UserStatus;

import java.util.List;

public class ChangeStatusRoleDto {
    private UserStatus newStatus;
    private List<String> roles;

    // Getters and setters
    public UserStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(UserStatus newStatus) {
        this.newStatus = newStatus;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
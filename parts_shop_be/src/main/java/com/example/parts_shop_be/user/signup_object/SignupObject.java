package com.example.parts_shop_be.user.signup_object;

import com.example.parts_shop_be.user.User;
import com.example.parts_shop_be.utils.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class SignupObject extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String randomUrl;

    private Boolean emailConfirmed;

    private LocalDateTime creationDate;

    private LocalDateTime confirmationDate;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRandomUrl() {
        return randomUrl;
    }

    public void setRandomUrl(String randomUrl) {
        this.randomUrl = randomUrl;
    }

    public SignupObject() {
    }

    public SignupObject(User user, String randomUrl, Boolean emailConfirmed) {
        this.user = user;
        this.randomUrl = randomUrl;
        this.emailConfirmed = emailConfirmed;
        this.creationDate = LocalDateTime.now();
    }

    public Boolean getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(LocalDateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }
}

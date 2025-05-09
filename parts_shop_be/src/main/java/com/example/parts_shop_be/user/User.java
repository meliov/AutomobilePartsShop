package com.example.parts_shop_be.user;


import com.example.parts_shop_be.utils.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDateTime registrationDate;

    private LocalDateTime updateDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private UserGender gender;
    @Column(nullable = false)
    private String email;

    @Embedded
    private CardDetails cardDetails;

    @Embeddable
    public class CardDetails {
        private String cardNumber;
        private String cardHolderName;
        private String expirationDate;
        private String cvv;

        public CardDetails(String cardNumber, String cardHolderName, String expirationDate, String cvv) {
            this.cardNumber = cardNumber;
            this.cardHolderName = cardHolderName;
            this.expirationDate = expirationDate;
            this.cvv = cvv;
        }

        public CardDetails() {
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }
    }

    public User(String firstName, String lastName, String username, String password, LocalDateTime registrationDate, UserStatus status, String email, UserGender userGender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.registrationDate = registrationDate;
        this.status = status;
        this.email = email;
        this.gender = userGender;
    }

    public User() {

    }


    public User(Long id) {
        super(id);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public UserGender getGender() {
        return gender;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(CardDetails cardDetails) {
        this.cardDetails = cardDetails;
    }
}

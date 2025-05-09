package com.example.parts_shop_be.user.dto;

import com.example.parts_shop_be.user.UserGender;

public class UpdateUserDto {
    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private UserGender gender;

    private String password;

    private CardDetailsDto cardDetailsDto;

    public UpdateUserDto() {
    }


    public UpdateUserDto(Long id, String firstName, String lastName, String username, String password, String email, UserGender userGender, CardDetailsDto cardDetailsDto) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = userGender;
        this.username = username;
        this.password = password;
        this.cardDetailsDto = cardDetailsDto;
    }

    public CardDetailsDto getCardDetailsDto() {
        return cardDetailsDto;
    }

    public void setCardDetailsDto(CardDetailsDto cardDetailsDto) {
        this.cardDetailsDto = cardDetailsDto;
    }

    class CardDetailsDto {
        private String cardNumber;
        private String cardHolderName;
        private String expirationDate;
        private String cvv;

        public CardDetailsDto(String cardNumber, String cardHolderName, String expirationDate, String cvv) {
            this.cardNumber = cardNumber;
            this.cardHolderName = cardHolderName;
            this.expirationDate = expirationDate;
            this.cvv = cvv;
        }

        public CardDetailsDto() {
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

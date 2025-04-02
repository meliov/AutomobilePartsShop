package com.example.parts_shop_be.utils.exception;

import java.io.Serializable;

public class UserAlreadyPresentException extends Exception implements Serializable {
    public UserAlreadyPresentException(String message) {
        super(message);
    }
}

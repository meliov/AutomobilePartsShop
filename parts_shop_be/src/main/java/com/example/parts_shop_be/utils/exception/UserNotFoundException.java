package com.example.parts_shop_be.utils.exception;

import java.io.Serializable;

public class UserNotFoundException extends Exception implements Serializable {
    public UserNotFoundException(String message) {
        super(message);
    }
}

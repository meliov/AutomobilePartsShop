package com.example.parts_shop_be.user.signup_object;

import com.example.parts_shop_be.user.User;

public interface SignupObjectService {
    SignupObject completeEmailConfirmation(String randomUrl);

    Boolean initiateSignup(User user, String externalLink);
}

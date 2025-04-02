package com.example.parts_shop_be.user.forgot_password_object;

import com.example.parts_shop_be.user.User;

public interface ForgotPasswordObjectService {
    ForgotPasswordObject completeChangePassword(String randomUrl);

    Boolean initialChangePassword(User user, String newPassword, String externalLink);
}

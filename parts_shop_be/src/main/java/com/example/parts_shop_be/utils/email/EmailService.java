package com.example.parts_shop_be.utils.email;

public interface EmailService {
    Boolean sendMail(String emailTo, String subject, String text);
}

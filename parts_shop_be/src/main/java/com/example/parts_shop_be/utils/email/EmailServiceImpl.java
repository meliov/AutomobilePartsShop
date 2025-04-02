package com.example.parts_shop_be.utils.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private static final String INTERNAL_EMAIL = "do-not-reply@test.com";

    @Override
    public Boolean sendMail(String emailTo, String subject, String text) {
        logger.info("Sending Mail to: " + emailTo + "\nsubject: " + subject);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailTo);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom(INTERNAL_EMAIL);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

}

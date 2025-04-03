package com.example.parts_shop_be.user.forgot_password_object;

import com.example.parts_shop_be.user.User;
import com.example.parts_shop_be.utils.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ForgotPasswordObjectServiceImpl implements ForgotPasswordObjectService {

    @Autowired
    private ForgotPasswordObjectRepository forgotPasswordObjectRepositoryObjectRepository;

    @Autowired
    private EmailService emailServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordObjectServiceImpl.class);

    @Override
    public ForgotPasswordObject completeChangePassword(String randomUrl) {
        logger.debug("in completeChangePassword");
        ForgotPasswordObject object = forgotPasswordObjectRepositoryObjectRepository.findSignupObjectsByRandomUrl(randomUrl).get();
        object.setEmailConfirmed(true);
        object.setConfirmationDate(LocalDateTime.now());
        forgotPasswordObjectRepositoryObjectRepository.save(object);
        return object;
    }

    @Override
    public Boolean initialChangePassword(User user, String newPassword, String externalLink) {
        logger.debug("in initialChangePassword");
        String randomUrl = UUID.randomUUID().toString();
        String constructLink = externalLink + randomUrl;

        String text = String.format("Hello %s,\nIf you initiated AutoShop (Activity Application) Password Change," +
                " then click the link below ONLY IF THIS WAS YOU\n%s\nIf this was not you, DO NOT do anything.", user.getUsername(), constructLink);

        forgotPasswordObjectRepositoryObjectRepository.save(new ForgotPasswordObject(user, randomUrl, false, newPassword));

        return emailServiceImpl.sendMail(user.getEmail(), "AutoShop, Forgot Password Confirmation", text);
    }
}

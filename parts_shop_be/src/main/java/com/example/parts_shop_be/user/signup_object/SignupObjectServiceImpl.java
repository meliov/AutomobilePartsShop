package com.example.parts_shop_be.user.signup_object;

import com.example.parts_shop_be.user.User;
import com.example.parts_shop_be.utils.email.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SignupObjectServiceImpl implements SignupObjectService {

    @Autowired
    private SignupObjectRepository signupObjectRepository;

    @Autowired
    private EmailServiceImpl emailServiceImpl;


    private static final Logger logger = LoggerFactory.getLogger(SignupObjectServiceImpl.class);

    @Override
    public SignupObject completeEmailConfirmation(String randomUrl) {
        logger.debug("in completeEmailConfirmation");
        SignupObject object = signupObjectRepository.findSignupObjectsByRandomUrl(randomUrl).get();
        object.setEmailConfirmed(true);
        object.setConfirmationDate(LocalDateTime.now());
        signupObjectRepository.save(object);
        return object;
    }

    @Override
    public Boolean initiateSignup(User user, String externalLink) {
        String randomUrl = UUID.randomUUID().toString();
        String constructLink = externalLink + randomUrl;

        String text = String.format("Hello %s,\nIf you initiated AutoShop (Activity Application) registration," +
                " then click the link below\n%s\nIf this was not you, don't do anything.", user.getUsername(), constructLink);
        signupObjectRepository.save(new SignupObject(user, randomUrl, false));

        return emailServiceImpl.sendMail(user.getEmail(), "AutoShop Signup Confirmation", text);
    }
}

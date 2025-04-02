package com.example.parts_shop_be.security.auth_user;



import com.example.parts_shop_be.user.User;
import com.example.parts_shop_be.user.UserRepository;
import com.example.parts_shop_be.user.UserStatus;
import com.example.parts_shop_be.user.signup_object.SignupObjectServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(SignupObjectServiceImpl.class);
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email).get();
        if (user == null) {
            String errorMessage = String.format("User %s not found in the database", email);
            logger.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        } else if (user.getStatus() != UserStatus.ACTIVE) {
            String errorMessage = String.format("User %s is not active. Status: %s", email, user.getStatus());
            logger.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }
        return new UserPrincipal(user);
    }


}

package com.example.parts_shop_be.user.service;

import com.example.parts_shop_be.user.*;
import com.example.parts_shop_be.user.dto.*;
import com.example.parts_shop_be.user.forgot_password_object.ForgotPasswordObject;
import com.example.parts_shop_be.user.forgot_password_object.ForgotPasswordObjectService;
import com.example.parts_shop_be.user.signup_object.SignupObject;
import com.example.parts_shop_be.user.signup_object.SignupObjectService;
import com.example.parts_shop_be.utils.exception.UserAlreadyPresentException;
import com.example.parts_shop_be.utils.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SignupObjectService signupObjectService;

    @Autowired
    private ForgotPasswordObjectService forgotPasswordObjectService;

    @Value("${host.and.port.for.email}")
    private String hostAndPort;
    @Override
    public ClientUserDto updateUser(UpdateUserDto updateUserDto) {
        User user = userRepository.findById(updateUserDto.getId()).get();
        user.setFirstName(updateUserDto.getFirstName());
        user.setGender(updateUserDto.getGender());
        user.setLastName(updateUserDto.getLastName());
        user.setCardDetails(modelMapper.map(updateUserDto.getCardDetailsDto(), User.CardDetails.class));
        user.setUpdateDate(LocalDateTime.now());
        if (updateUserDto.getPassword() != null && updateUserDto.getPassword().length() >= 8) {
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }
        user.setUsername(updateUserDto.getUsername());

        return modelMapper.map(userRepository.save(user), ClientUserDto.class);
    }

    @Override
    public ClientUserDto getUserDto(Long id) {
        return modelMapper.map(userRepository.findById(id), ClientUserDto.class);
    }

    @Override
    @Transactional
    public ClientUserDto createUser(CreateUserDto userDto, UserStatus userStatus) throws UserAlreadyPresentException {
        if (userRepository.findUserByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyPresentException("User already exists");
        }
        var user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFirstName("");
        user.setLastName("");
        user.setStatus(userStatus);
        user.setRegistrationDate(LocalDateTime.now());
        user.setGender(UserGender.NONE);

        User newUser = userRepository.save(user);
        String link = hostAndPort + "/user/signup-confirmation/";

        if (signupObjectService.initiateSignup(newUser, link)) {
            return modelMapper.map(user, ClientUserDto.class);
        } else {
            throw new RuntimeException("User signup initiation went wrong!");
        }
    }

    @Override
    public Boolean checkIfUserExistsByEmail(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    @Override
    public Boolean checkIfUSerExistsForResetPasswordByEmail(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        return user.isPresent() && user.get().getStatus() == UserStatus.ACTIVE;
    }

    @Override
    @Transactional
    public Boolean confirmUserSignup(String randomUrl) {
        try {
            SignupObject signupObject = signupObjectService.completeEmailConfirmation(randomUrl);
            User user = userRepository.findById(signupObject.getUser().getId()).get();
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("confirmUserSignup went wrong:\n" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Boolean initiateResetPassword(ResetPasswordDto resetPasswordDto) throws UserNotFoundException {
        User user = userRepository.findUserByEmail(resetPasswordDto.getEmail()).get();

        if (user == null) {
            throw new UserNotFoundException("User already exists");
        }
//        user.setStatus(UserStatus.CHANGE_PASSWORD);
        User updatedUser = userRepository.save(user);

        String link = hostAndPort + "/user/forgot-password-confirmation/";

        if (forgotPasswordObjectService.initialChangePassword(updatedUser, passwordEncoder.encode(resetPasswordDto.getPassword()), link)) {
            return true;
        } else {
            throw new RuntimeException("User signup initiation went wrong!");
        }
    }

    @Override
    public ClientUserDto findObjectUserByEmail(String email) {
        var x = modelMapper.map(userRepository.findUserByEmail(email), ClientUserDto.class);
        return modelMapper.map(userRepository.findUserByEmail(email), ClientUserDto.class);
    }
    @Override
    @Transactional
    public Boolean confirmUserResetPassword(String randomUrl) {
        try {
            ForgotPasswordObject forgotPasswordObject = forgotPasswordObjectService.completeChangePassword(randomUrl);
            User user = userRepository.findById(forgotPasswordObject.getUser().getId()).get();
            user.setStatus(UserStatus.ACTIVE);
            user.setPassword(forgotPasswordObject.getNewPassword());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("confirmUserSignup went wrong:\n" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updatePassword(ChangePasswordDto changePasswordDto) throws UserNotFoundException {
        // Fetch the user by ID
        User user = userRepository.findById(changePasswordDto.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate the old password
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        return true;
    }

}

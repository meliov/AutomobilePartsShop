package com.example.parts_shop_be.user.service;

import com.example.parts_shop_be.user.*;
import com.example.parts_shop_be.user.card_details.CardDetails;
import com.example.parts_shop_be.user.card_details.CardDetailsDto;
import com.example.parts_shop_be.user.dto.*;
import com.example.parts_shop_be.user.forgot_password_object.ForgotPasswordObject;
import com.example.parts_shop_be.user.forgot_password_object.ForgotPasswordObjectService;
import com.example.parts_shop_be.user.role.UserRole;
import com.example.parts_shop_be.user.signup_object.SignupObject;
import com.example.parts_shop_be.user.signup_object.SignupObjectService;
import com.example.parts_shop_be.utils.email.EmailService;
import com.example.parts_shop_be.utils.exception.UserAlreadyPresentException;
import com.example.parts_shop_be.utils.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private EmailService emailServiceImpl;
    @Override
    public ClientUserDto updateUser(UpdateUserDto updateUserDto) {
        User user = userRepository.findById(updateUserDto.getId()).get();
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setUpdateDate(LocalDateTime.now());
        user.setAddress(updateUserDto.getAddress());
        user.setUsername(updateUserDto.getUsername());

        return modelMapper.map(userRepository.save(user), ClientUserDto.class);
    }

    @Override
    public ClientUserDto updateUserCardDetails(Long userId, CardDetailsDto cardDetailsDto) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setCardDetails(modelMapper.map(cardDetailsDto, CardDetails.class));

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, ClientUserDto.class);
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
        user.setRoles(List.of(UserRole.USER));

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

    @Transactional
    @Override
    public Boolean changeUserStatusOrRoleAndNotify(Long userId, UserStatus newStatus, List<UserRole> roles) throws UserNotFoundException {
        // Fetch the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Prepare email content
        StringBuilder emailContent = new StringBuilder();
        emailContent.append(String.format("Dear %s,\n\n", user.getFirstName()));

        // Update the user's status if changed
        if (user.getStatus() != newStatus) {
            user.setStatus(newStatus);
            emailContent.append(String.format("Your account status has been updated to: %s.\n", newStatus.name()));
        }

        // Update the user's role if changed
        if (!new HashSet<>(user.getRoles()).containsAll(roles) || !new HashSet<>(roles).containsAll(user.getRoles())) {
            user.setRoles(roles);
            emailContent.append(String.format("Your account roles have been updated to: %s.\n", roles));
        }

        // Save the updated user
        userRepository.save(user);

        // Complete email content
        emailContent.append("\nPlease contact support if you have any questions.\n\nBest regards,\nThe Parts Shop Team");

        // Send notification email
        emailServiceImpl.sendMail(user.getEmail(), "Account Update Notification", emailContent.toString());

        return true;
    }


    @Override
    public List<ClientUserDto> getUserDtos() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, ClientUserDto.class)).collect(Collectors.toList());
    }
}

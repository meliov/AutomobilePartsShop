package com.example.parts_shop_be.user;

import com.example.parts_shop_be.user.dto.ClientUserDto;
import com.example.parts_shop_be.user.dto.CreateUserDto;
import com.example.parts_shop_be.user.dto.ResetPasswordDto;
import com.example.parts_shop_be.user.dto.UpdateUserDto;
import com.example.parts_shop_be.user.service.UserService;
import com.example.parts_shop_be.utils.exception.UserAlreadyPresentException;
import com.example.parts_shop_be.utils.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
class UserController {
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("user test");
        logger.error("user test");
        logger.warn("user test");
        logger.debug("user test");
        logger.trace("user test");
        return ResponseEntity.ok("user test");
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        logger.debug("In getUser, for userId: " + userId);
        return ResponseEntity.ok(
                userService.getUserDto(userId)
        );
    }

    @GetMapping("/email-exists/{email}")
    public ResponseEntity<Object> checkIfUserExistsByMail(@PathVariable String email) {
        logger.debug("In checkIfUserExistsByMail, for email: " + email);
        return ResponseEntity.ok(
                userService.checkIfUserExistsByEmail(email)
        );
    }

    @GetMapping("/email-exists-pass-reset/{email}")
    public ResponseEntity<Object> checkIfUserExistsForPassResetByMail(@PathVariable String email) {
        logger.debug("In checkIfUserExistsByMail, for email: " + email);
        return ResponseEntity.ok(
                userService.checkIfUSerExistsForResetPasswordByEmail(email)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody CreateUserDto createUserDto) {
        logger.debug("in registerUser for user:" + createUserDto.getEmail());
        try {
            ClientUserDto user = userService.createUser(createUserDto, UserStatus.PENDING);
            if (user != null) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        } catch (UserAlreadyPresentException e) {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("User Exists..");
        }
    }

    @GetMapping("/signup-confirmation/{randomUrl}")
    public ResponseEntity<Object> confirmSignup(@PathVariable String randomUrl) {
        logger.debug("In confirmSignup, for random url: " + randomUrl);
        String responseMessage;
        if (userService.confirmUserSignup(randomUrl)) {
            responseMessage = "Thank you for signing up!";
        } else {
            responseMessage = "Something went wrong..";
        }
        return ResponseEntity.ok(
                responseMessage
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> initiateResetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        logger.debug("in initiateResetPassword for user:" + resetPasswordDto.getEmail());
        try {
            return ResponseEntity.ok(userService.initiateResetPassword(resetPasswordDto));
        } catch (UserNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("User Was not found..");
        }
    }

    @GetMapping("/forgot-password-confirmation/{randomUrl}")
    public ResponseEntity<Object> confirmResetPassword(@PathVariable String randomUrl) {
        logger.debug("In confirmResetPassword, for random url: " + randomUrl);
        String responseMessage;
        if (userService.confirmUserResetPassword(randomUrl)) {
            responseMessage = "Successfully changed your password!";
        } else {
            responseMessage = "Something went wrong..";
        }
        return ResponseEntity.ok(
                responseMessage
        );
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        logger.debug("in updateUser for user:" + updateUserDto.getEmail());
        return ResponseEntity.ok(
                userService.updateUser(updateUserDto)
        );
    }



}

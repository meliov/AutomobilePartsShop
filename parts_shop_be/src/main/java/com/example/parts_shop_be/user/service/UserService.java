package com.example.parts_shop_be.user.service;

import com.example.parts_shop_be.user.UserStatus;
import com.example.parts_shop_be.user.card_details.CardDetailsDto;
import com.example.parts_shop_be.user.dto.*;
import com.example.parts_shop_be.user.role.UserRole;
import com.example.parts_shop_be.utils.exception.UserAlreadyPresentException;
import com.example.parts_shop_be.utils.exception.UserNotFoundException;

import javax.transaction.Transactional;
import java.util.List;

public interface UserService {

    ClientUserDto updateUser(UpdateUserDto updateUserDto);

    ClientUserDto updateUserCardDetails(Long userId, CardDetailsDto cardDetailsDto) throws UserNotFoundException;

    ClientUserDto getUserDto(Long id);

    ClientUserDto createUser(CreateUserDto user, UserStatus userStatus) throws UserAlreadyPresentException;

    Boolean checkIfUserExistsByEmail(String email);

    Boolean checkIfUSerExistsForResetPasswordByEmail(String email);

    Boolean confirmUserSignup(String randomUrl);

    Boolean initiateResetPassword(ResetPasswordDto resetPasswordDto) throws UserNotFoundException;

    ClientUserDto findObjectUserByEmail(String email);

    Boolean confirmUserResetPassword(String randomUrl);


    boolean updatePassword(ChangePasswordDto changePasswordDto) throws UserNotFoundException;

    Boolean  changeUserStatusOrRoleAndNotify(Long userId, UserStatus newStatus, List<UserRole> roles)  throws UserNotFoundException;

    List<ClientUserDto> getUserDtos(Long userId);
}

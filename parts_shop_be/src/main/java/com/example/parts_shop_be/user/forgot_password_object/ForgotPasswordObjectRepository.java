package com.example.parts_shop_be.user.forgot_password_object;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordObjectRepository extends JpaRepository<ForgotPasswordObject, Long> {

    Optional<ForgotPasswordObject> findSignupObjectsByRandomUrl(String randomUrl);

}

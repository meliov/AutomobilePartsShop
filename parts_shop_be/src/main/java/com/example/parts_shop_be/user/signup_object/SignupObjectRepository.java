package com.example.parts_shop_be.user.signup_object;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignupObjectRepository extends JpaRepository<SignupObject, Long> {

    Optional<SignupObject> findSignupObjectsByRandomUrl(String randomUrl);

}

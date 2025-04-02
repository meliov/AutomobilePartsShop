package com.example.parts_shop_be.security.filter;


import com.example.parts_shop_be.security.JwtTokenProvider;
import com.example.parts_shop_be.security.auth_user.UserPrincipal;
import com.example.parts_shop_be.user.UserSource;
import com.example.parts_shop_be.user.UserStatus;
import com.example.parts_shop_be.user.dto.ClientUserDto;
import com.example.parts_shop_be.user.dto.CreateUserDto;
import com.example.parts_shop_be.user.service.UserService;
import com.example.parts_shop_be.utils.exception.UserAlreadyPresentException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final ModelMapper modelMapper;

    private static final String MOCKED_GOOGLE_PASSWORD = "MOCKED_GOOGLE_PASSWORD";


    private final UserService userService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, ModelMapper modelMapper, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        Map<String, Object> map = parseRequestBody(request); // Extract request body
        System.out.println();
        if (map.containsKey("googleIdToken") && map.get("googleIdToken") != null) {
            // Handle Google authentication
            String googleIdToken = (String) map.get("googleIdToken");
            String username = (String) map.get("username");
            String email = validateGoogleTokenAndGetEmail(googleIdToken); // Implement this
            return createAuthenticationForGoogleUser(email, username);
        } else {
            // Internal email/password flow
            String email = (String) map.get("email");
            String password = (String) map.get("password");
            if (password.equals(MOCKED_GOOGLE_PASSWORD)) {
                throw new RuntimeException("Wrong Auth Method for this user. Try Google Login!");
            }
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        }
    }

    private Map<String, Object> parseRequestBody(HttpServletRequest request) throws AuthenticationException {
        try {
            StringBuilder jsonBody = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
            return new Gson().fromJson(jsonBody.toString(), Map.class);
        } catch (Exception e) {
            throw new AuthenticationException("Failed to parse request") {
            };
        }
    }

    public String validateGoogleTokenAndGetEmail(String idToken) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            GsonFactory jsonFactory = new GsonFactory();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList("158083943420-vn4hc6ihp8jsi9pm6ehqskr3md85on98.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                return payload.getEmail();
            } else {
                throw new RuntimeException("Invalid Google ID token.");
            }
        } catch (Exception e) {
            logger.error("Error verifying Google token: " + e.getMessage(), e);
            throw new RuntimeException("Error verifying Google token: " + e.getMessage(), e);
        }
    }

    /**
     * here we are always sure that the user has valid email so we can create it or fetch it from the db so the jwt can be acquired
     * IF google user exists, here he is being authenticated, if he doesnt exist we create him and authenticate him
     */
    private Authentication createAuthenticationForGoogleUser(String email, String username) {
        if (!userService.checkIfUserExistsByEmail(email)) {
            try {
                userService.createUser(new CreateUserDto(MOCKED_GOOGLE_PASSWORD, email, username, UserSource.GOOGLE), UserStatus.ACTIVE);
            } catch (UserAlreadyPresentException e) {
                throw new RuntimeException(e);
            }
        }

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, MOCKED_GOOGLE_PASSWORD)
        );
    }
    //
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtTokenProvider.generateJwtToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);
        response.setHeader("Jwt-Token", token);
        response.setHeader("Jwt-Refresh-Token", refreshToken);
        ClientUserDto userDto = this.modelMapper.map(userPrincipal.getUser(), ClientUserDto.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), userDto);

    }

}

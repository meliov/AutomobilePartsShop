package com.example.parts_shop_be.security.filter;


import com.example.parts_shop_be.security.JwtTokenProvider;
import com.example.parts_shop_be.security.auth_user.UserPrincipal;
import com.example.parts_shop_be.user.dto.ClientUserDto;
import com.example.parts_shop_be.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final ModelMapper modelMapper;


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
        // Internal email/password flow
        String email = (String) map.get("email");
        String password = (String) map.get("password");

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
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

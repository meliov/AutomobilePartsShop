package com.example.parts_shop_be.security;


import com.example.parts_shop_be.security.auth_user.UserPrincipal;
import com.example.parts_shop_be.user.User;
import com.example.parts_shop_be.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class SecurityController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/api/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String refreshToken = authorizationHeader.substring("Bearer ".length());
            String username = jwtTokenProvider.getSubject(refreshToken);
            if (jwtTokenProvider.isTokenValid(refreshToken)) {

                UserPrincipal user = new UserPrincipal(modelMapper.map(this.userService.findUserByEmail(username), User.class));
                String newAccessToken = jwtTokenProvider.generateJwtToken(user);

                response.setHeader("Jwt-Token", newAccessToken);
                response.setHeader("Jwt-Refresh-Token", refreshToken);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}

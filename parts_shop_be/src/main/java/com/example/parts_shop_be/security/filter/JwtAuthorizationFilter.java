package com.example.parts_shop_be.security.filter;


import com.example.parts_shop_be.security.HttpResponse;
import com.example.parts_shop_be.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import static com.example.parts_shop_be.security.SecurityConfig.PUBLIC_URLS;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            boolean pathIsPublic = Arrays.stream(PUBLIC_URLS).anyMatch(it -> request.getServletPath().contains(it));
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ") || pathIsPublic) {//[propuskame publichnite urli
                filterChain.doFilter(request, response);
            } else {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    String username = tokenProvider.getSubject(token);
                    if (tokenProvider.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                        List<GrantedAuthority> authorities = tokenProvider.getAuthority(token);
                        //tva samo si pravi i vkarva authentication obekt, to veche e minalo authentication
                        Authentication authentication = tokenProvider.getAuthentication(username, authorities, request);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                } catch (Exception e) {

                    SecurityContextHolder.clearContext();
                    HttpResponse httpResponse = new HttpResponse(
                            new Date(),
                            UNAUTHORIZED.value(),
                            UNAUTHORIZED,
                            UNAUTHORIZED.getReasonPhrase().toUpperCase(),
                            "Token has expired!");

                    response.setContentType(APPLICATION_JSON_VALUE);
                    response.setStatus(UNAUTHORIZED.value());

                    OutputStream outputStream = response.getOutputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(outputStream, httpResponse);
                    outputStream.flush();
                }
                filterChain.doFilter(request, response);
            }
        }
    }
}

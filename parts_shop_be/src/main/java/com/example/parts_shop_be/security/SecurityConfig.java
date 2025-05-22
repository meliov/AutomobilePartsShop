package com.example.parts_shop_be.security;

import com.example.parts_shop_be.security.filter.AuthenticationEntryPoint;
import com.example.parts_shop_be.security.filter.JwtAccessDeniedHandler;
import com.example.parts_shop_be.security.filter.JwtAuthenticationFilter;
import com.example.parts_shop_be.security.filter.JwtAuthorizationFilter;
import com.example.parts_shop_be.user.role.UserRole;
import com.example.parts_shop_be.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    //public urls are checked twice - once before authentication - where its permitted and once during auth
    public static final String[] PUBLIC_URLS = {
            "/products/**",
            "/rates**",
            "/user/test", "/api/login", "/api/token/refresh",
            "/user/email-exists/*", "/user/email-exists-pass-reset/*",
            "/user/register", "/user/signup-confirmation/*", "/user/reset-password",
            "/user/forgot-password-confirmation/*",
            "/orders/save/**"
    };//, "/api/v1/user/all"

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManagerBean(), jwtTokenProvider, modelMapper, userService);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http
                .cors().and().
                csrf().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                        .authorizeRequests().antMatchers(PUBLIC_URLS).permitAll().and()
                        .authorizeRequests().anyRequest().authenticated().and().
                //for future roles based authorization
                //.antMatchers("/admin/**").hasRole("ADMIN") // Only ADMIN can access /admin routes
                //            .antMatchers("/user/**").hasAnyRole("USER", "ADMIN") // USER and ADMIN can access /user routes
                exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(jwtAccessDeniedHandler).and().
                addFilter(jwtAuthenticationFilter).
                addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("append", "delete", "entries", "foreach", "get", "has", "keys", "set", "values", "Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Jwt-Token", "Jwt-Refresh-Token", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Jwt-Refresh-Token", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

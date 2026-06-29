package com.epam.travel_agency_final_project.config;
import com.epam.travel_agency_final_project.repository.UserRepository;
import com.epam.travel_agency_final_project.security.JwtCookieFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final UserRepository userRepository;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtCookieFilter jwtCookieFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/tours/**", "/cart/**", "/book/**", "/error/**",
                                        "/blocked/**", "/register", "/login","/api/**",
                                        "/css/**", "/js/**", "/images/**", "/uploads/**", "/error").permitAll()
                        .requestMatchers("/admin/create-tour", "/admin/tours/create", "/admin/tour-createdInfo")
                        .hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/admin/users", "/admin/users/lock/**", "/admin/invalid-email")
                        .hasRole("ADMIN")
                        .requestMatchers("/profile/**").authenticated()
                        .anyRequest().authenticated())
                        .exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/login");
                        })
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.logoutSuccessUrl("/tours").permitAll())
                .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
    @Bean
    public SpringSecurityDialect springSecurityDialect() {

        return new SpringSecurityDialect();
    }
}
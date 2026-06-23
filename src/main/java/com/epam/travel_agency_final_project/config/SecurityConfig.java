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
//    @Bean
// public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtCookieFilter jwtCookieFilter) throws Exception {
//     http
//             .csrf(csrf -> csrf.disable())
//             .sessionManagement(session -> session
//                     .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Для JWT краще використовувати STATELESS
//             )
//             .authorizeHttpRequests(auth -> auth
//                     .requestMatchers(
//                             "/", "/index", "/tours/**", "/cart/**", "/book/**",
//                             "/blocked/**", "/register", "/login",
//                             "/css/**", "/js/**", "/images/**", "/uploads/**", "/error"
//                     ).permitAll()
//                     .requestMatchers("/profile/**").authenticated() // Тільки авторизовані можуть бачити профіль
//                     .anyRequest().authenticated()
//             )
//             .exceptionHandling(exception -> exception
//                     .authenticationEntryPoint((request, response, authException) -> {
//                         // Це спрацює, коли користувач неавторизований (403 або 401)
//                         response.sendRedirect("/login");
//                     })
//             )
//             .formLogin(form -> form.disable())
//             .logout(logout -> logout.logoutSuccessUrl("/tours").permitAll())
//             .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);
//
//
//     return http.build();
// }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtCookieFilter jwtCookieFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                // 1. Публічні сторінки

                                .requestMatchers("/", "/index", "/tours/**", "/cart/**", "/book/**",
                                        "/blocked/**", "/register", "/login",
                                        "/css/**", "/js/**", "/images/**", "/uploads/**", "/error").permitAll()


                                // 2. Доступ для ролей MANAGER та ADMIN
                                .requestMatchers("/admin/create-tour", "/admin/tours/create", "/admin/tour-createdInfo")
                                .hasAnyRole("MANAGER", "ADMIN")

                                // 3. Доступ ТІЛЬКИ для ADMIN
                                .requestMatchers("/admin/users", "/admin/users/lock/**", "/admin/invalid-email")
                                .hasRole("ADMIN")

                                // 4. Тільки авторизовані користувачі (базовий рівень)
                                .requestMatchers("/profile/**").authenticated() // Тільки авторизовані можуть бачити профіль
//                     .anyRequest().authenticated()
                                // 5. Всі інші запити повинні бути авторизовані
                                .anyRequest().authenticated()
                )


                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Це спрацює, коли користувач неавторизований (403 або 401)
                            response.sendRedirect("/login");
                        })
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.logoutSuccessUrl("/tours").permitAll())
                .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
//     return http.build();
//
// ... решта налаштувань
              //  .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);

      //  return http.build();

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Користувач не знайдений: " + email));

            return new User(
                    user.getEmail(),
                    user.getPasswordHash(),
                    Collections.emptyList()
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }


    private final UserRepository userRepository;

}
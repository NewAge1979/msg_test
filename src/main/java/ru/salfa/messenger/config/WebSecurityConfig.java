package ru.salfa.messenger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(
                        cors -> cors.configurationSource(
                                request -> {
                                    var corsConfiguration = new CorsConfiguration();
                                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                                    corsConfiguration.setAllowedMethods(List.of("*"));
                                    corsConfiguration.setAllowedHeaders(List.of("*"));
                                    corsConfiguration.setAllowCredentials(true);
                                    return corsConfiguration;
                                }
                        )
                )
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/api/v1/auth/getOTPcode", "/api/v1/auth/signUp", "/api/v1/auth/signIn").permitAll()
                                .requestMatchers("/swagger-ui/**", "/swagger-resource/*", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
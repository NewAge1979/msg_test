package ru.salfa.messenger.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import ru.salfa.messenger.repository.UserRepository;
import ru.salfa.messenger.security.JwtEntryPoint;
import ru.salfa.messenger.security.JwtFilter;
import ru.salfa.messenger.security.PhoneOtpAuthenticationProvider;
import ru.salfa.messenger.service.OtpService;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final static String[] WHITELIST = {
            "/api/v1/auth/getOTPCode",
            "/api/v1/auth/signIn",
            "/swagger-ui/**",
            "/swagger-resource/*",
            "/v3/api-docs/**"
    };

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;
    private final JwtEntryPoint jwtEntryPoint;
    private final UserRepository userRepository;
    private final OtpService otpService;

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
                                .requestMatchers(WHITELIST).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtEntryPoint))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        PhoneOtpAuthenticationProvider phoneOtpAuthenticationProvider = new PhoneOtpAuthenticationProvider(userRepository, otpService);
        phoneOtpAuthenticationProvider.setUserDetailsService(userDetailsService);
        phoneOtpAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return phoneOtpAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
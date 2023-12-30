package com.backend.restservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf((csrf) -> csrf.disable());
                http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                                .requestMatchers("/api/sessions/**").hasRole("USER"));

                // Allow h2-console and swagger ui to be accessed and render for demo purposes
                http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                                .anyRequest().permitAll());
                http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()));

                http.formLogin(withDefaults());
                http.httpBasic(withDefaults());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}

package com.drapson.springauthtutorial.config;

import com.drapson.springauthtutorial.adapters.in.security.OAuth2CodeServiceImpl;
import com.drapson.springauthtutorial.adapters.in.security.UserDetailsServiceImpl;
import com.drapson.springauthtutorial.application.*;
import com.drapson.springauthtutorial.application.out.OAuth2CodeService;
import com.drapson.springauthtutorial.application.out.RefreshTokenRepository;
import com.drapson.springauthtutorial.application.out.TokenProvider;
import com.drapson.springauthtutorial.application.out.UserProviderRepository;
import com.drapson.springauthtutorial.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceConfiguration {

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public AuthService authService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            BCryptPasswordEncoder passwordEncoder,
            TokenProvider tokenProvider
    ) {
        return new AuthServiceImpl(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                tokenProvider
        );
    }

    @Bean
    public OAuth2Service oAuth2Service(
            UserRepository userRepository,
            UserProviderRepository userProviderRepository,
            OAuth2CodeService oAuth2CodeService,
            AuthService authService
    ) {
        return new OAuth2ServiceImpl(
                userRepository,
                userProviderRepository,
                oAuth2CodeService,
                authService
        );
    }

    @Bean
    public OAuth2CodeService oAuth2CodeService(
            WebClient webClient,
            @Value("${spring.security.oauth2.client.registration.google.client-id}") String googleClientId,
            @Value("${spring.security.oauth2.client.registration.google.client-secret}") String googleClientSecret
    ) {
        return new OAuth2CodeServiceImpl(webClient, googleClientId, googleClientSecret);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

}

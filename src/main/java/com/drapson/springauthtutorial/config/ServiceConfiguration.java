package com.drapson.springauthtutorial.config;

import com.drapson.springauthtutorial.application.*;
import com.drapson.springauthtutorial.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
            UserProviderRepository userProviderRepository,
            BCryptPasswordEncoder passwordEncoder,
            TokenProvider tokenProvider,
            TempUserDataPort tempUserDataPort

    ) {
        return new AuthServiceImpl(userRepository, refreshTokenRepository, userProviderRepository, passwordEncoder, tokenProvider, tempUserDataPort);
    }

}

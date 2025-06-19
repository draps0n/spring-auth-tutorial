package com.drapson.springauthtutorial.adapters.in.security;

import com.drapson.springauthtutorial.application.AuthService;
import com.drapson.springauthtutorial.application.TempUserDataPort;
import com.drapson.springauthtutorial.application.UserService;
import com.drapson.springauthtutorial.application.dtos.AuthTokens;
import com.drapson.springauthtutorial.application.dtos.PendingOAuthRegistration;
import com.drapson.springauthtutorial.domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.time.Duration;
import java.util.UUID;

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final AuthService authService;
    private final TempUserDataPort tempUserDataPort;
    private final String frontUrl;

    public CustomOAuth2SuccessHandler(UserService userService, AuthService authService, TempUserDataPort tempUserDataPort, String frontUrl) {
        this.userService = userService;
        this.authService = authService;
        this.tempUserDataPort = tempUserDataPort;
        this.frontUrl = frontUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        // For now only sub is supported as a unique identifier
        String sub = oAuth2User.getAttribute("sub");
        if (sub == null) {
            throw new ServletException("sub is required");
        }

        User user = userService.getUserByEmail(email).orElse(null);

        PendingOAuthRegistration pendingOAuthRegistration = new PendingOAuthRegistration(
                provider, sub, email, firstName, lastName
        );
        if (user == null) {
            String registrationToken = UUID.randomUUID().toString();
            tempUserDataPort.save(registrationToken, pendingOAuthRegistration, Duration.ofMinutes(10)); // TODO: make it configurable
            System.out.println("User not found, redirecting to registration page: " + email);
            throw new AdditionalRegistrationInfoNeededException("To register, please provide additional information.", registrationToken);
        } else if (!authService.checkIfUserHasProvider(user, provider)) {
            String linkToken = UUID.randomUUID().toString();
            tempUserDataPort.save(linkToken, pendingOAuthRegistration, Duration.ofMinutes(5)); // TODO: make it configurable
            System.out.println("User found but provider not linked, redirecting to link account page: " + email);
            throw new EmailLinkedToAnotherAccountWithDifferentProviderException("Account linking needed.", linkToken);
        } else {
            AuthTokens authTokens = authService.issueJwtTokens(user);
            response.setContentType("application/json");
            System.out.println("User logged in successfully: " + user.getEmail());
            // TODO: issue cookies
        }
    }
}

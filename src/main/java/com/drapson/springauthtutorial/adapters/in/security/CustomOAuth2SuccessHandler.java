package com.drapson.springauthtutorial.adapters.in.security;

import com.drapson.springauthtutorial.application.AuthService;
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

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final AuthService authService;
    private final String frontUrl;

    public CustomOAuth2SuccessHandler(UserService userService, AuthService authService, String frontUrl) {
        this.userService = userService;
        this.authService = authService;
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
        String sub = oAuth2User.getAttribute("sub");

        if (sub == null) {
            throw new ServletException("sub is required");
        }

        User user = userService.getUserByEmail(email).orElse(null);

        PendingOAuthRegistration pendingOAuthRegistration =
                new PendingOAuthRegistration(provider, sub, email, firstName, lastName);

        issueTokens(response, user, provider, pendingOAuthRegistration);
    }

    private void issueTokens(HttpServletResponse response, User user, String provider, PendingOAuthRegistration pendingOAuthRegistration) {
        if (user == null) {
            // User not found, more registration details needed
            String tempRegistrationToken = authService.issueTemporaryRegistrationToken(pendingOAuthRegistration);
            throw new AdditionalRegistrationInfoNeededException("To register, please provide additional information.", tempRegistrationToken);
        } else if (!authService.checkIfUserHasProvider(user, provider)) {
            // User found but provider is not linked, possible account linking needed
            String linkToken = authService.issueTemporaryRegistrationToken(pendingOAuthRegistration);
            throw new EmailLinkedToAnotherAccountWithDifferentProviderException("Account linking needed.", linkToken);
        } else {
            // User found and provider is linked, issue JWT tokens
            AuthTokens authTokens = authService.issueJwtTokens(user);
            response.setContentType("application/json");
            // TODO: issue cookies
        }
    }

}

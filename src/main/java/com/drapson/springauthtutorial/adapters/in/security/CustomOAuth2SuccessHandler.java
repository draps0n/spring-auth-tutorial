package com.drapson.springauthtutorial.adapters.in.security;

import com.drapson.springauthtutorial.adapters.in.api.ErrorCode;
import com.drapson.springauthtutorial.adapters.in.api.ProblemDetailDto;
import com.drapson.springauthtutorial.adapters.in.api.response.AccessTokenResponse;
import com.drapson.springauthtutorial.adapters.in.api.util.CookieUtil;
import com.drapson.springauthtutorial.application.AuthService;
import com.drapson.springauthtutorial.application.UserService;
import com.drapson.springauthtutorial.application.dtos.AuthTokens;
import com.drapson.springauthtutorial.application.dtos.PendingOAuthRegistration;
import com.drapson.springauthtutorial.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final ObjectMapper objectMapper;
    private final String frontendCallbackURL;

    public CustomOAuth2SuccessHandler(
            UserService userService,
            AuthService authService,
            CookieUtil cookieUtil,
            ObjectMapper objectMapper,
            String frontendCallbackURL
    ) {
        this.userService = userService;
        this.authService = authService;
        this.cookieUtil = cookieUtil;
        this.objectMapper = objectMapper;
        this.frontendCallbackURL = frontendCallbackURL;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
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

        User user = userService.getUserByEmail(email).orElse(null); // getUserBySub() / ProviderId()

        PendingOAuthRegistration pendingOAuthRegistration =
                new PendingOAuthRegistration(provider, sub, email, firstName, lastName);

        if (user == null) {
            // User not found, more registration details needed
            processOAuth2Registration(response, pendingOAuthRegistration);
        } else if (!authService.checkIfUserHasProvider(user, provider)) {
            // User found but provider is not linked, possible account linking needed
            processPossibleOAuth2AccountLinking(response, pendingOAuthRegistration);
        } else {
            // User found and provider is linked, issue JWT tokens
            processStandardOAuth2SignIn(response, user);
        }
    }

    private void processOAuth2Registration(HttpServletResponse response, PendingOAuthRegistration pendingOAuthRegistration) throws IOException {
        String registrationToken = authService.issueTemporaryRegistrationToken(pendingOAuthRegistration);
        String redirectUrl = String.format("%s?status=registration_required&registrationToken=%s", frontendCallbackURL, registrationToken);
        response.sendRedirect(redirectUrl);
    }

    private void processPossibleOAuth2AccountLinking(HttpServletResponse response, PendingOAuthRegistration pendingOAuthRegistration) throws IOException {
        String linkToken = authService.issueTemporaryRegistrationToken(pendingOAuthRegistration);
        String redirectUrl = String.format("%s?status=linking_required&linkToken=%s", frontendCallbackURL, linkToken);
        response.sendRedirect(redirectUrl);
    }

    private void processStandardOAuth2SignIn(HttpServletResponse response, User user) {
        AuthTokens authTokens = authService.issueJwtTokens(user);
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(authTokens.accessToken());
        Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authTokens.refreshToken());
        response.addCookie(refreshTokenCookie);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        try {
            String json = objectMapper.writeValueAsString(accessTokenResponse);
            response.getWriter().write(json);
        } catch (Exception e) {
            throw new RuntimeException("Error writing response: ", e);
        }
    }

}

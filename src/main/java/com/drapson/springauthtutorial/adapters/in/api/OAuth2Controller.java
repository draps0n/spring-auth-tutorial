package com.drapson.springauthtutorial.adapters.in.api;

import com.drapson.springauthtutorial.adapters.in.api.request.LinkOAuthAccountRequest;
import com.drapson.springauthtutorial.adapters.in.api.request.OAuthCodeRequest;
import com.drapson.springauthtutorial.adapters.in.api.response.OAuth2LinkResponse;
import com.drapson.springauthtutorial.adapters.in.api.util.CookieUtil;
import com.drapson.springauthtutorial.application.OAuth2Service;
import com.drapson.springauthtutorial.application.dtos.AuthTokens;
import com.drapson.springauthtutorial.application.dtos.GoogleLoginDTO;
import com.drapson.springauthtutorial.application.dtos.LinkOAuthAccountDto;
import com.drapson.springauthtutorial.application.dtos.OAuthCodeDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidParameterException;
import java.util.Optional;

@SecurityRequirements
@RestController
@RequestMapping("/api/v1/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;
    private final CookieUtil cookieUtil;

    public OAuth2Controller(OAuth2Service oAuth2Service, CookieUtil cookieUtil) {
        this.oAuth2Service = oAuth2Service;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/link")
    public ResponseEntity<Void> linkOAuthAccounts(@RequestBody @Valid LinkOAuthAccountRequest linkOAuthAccountRequest,
                                                  HttpServletResponse response) {
        if (!linkOAuthAccountRequest.validate()) {
            throw new InvalidParameterException("If shouldLink is true, provider, providerId, userId, and password must be provided.");
        }

        Optional<AuthTokens> authTokens = oAuth2Service.linkNewOAuthAccount(new LinkOAuthAccountDto(
                linkOAuthAccountRequest.shouldLink(),
                linkOAuthAccountRequest.provider(),
                linkOAuthAccountRequest.providerId(),
                linkOAuthAccountRequest.userId(),
                linkOAuthAccountRequest.password()
        ));

        if (authTokens.isPresent()) {
            AuthTokens createdTokens = authTokens.get();

            Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(createdTokens.refreshToken());
            Cookie accessTokenCookie = cookieUtil.createAccessTokenCookie(createdTokens.accessToken());

            response.addCookie(refreshTokenCookie);
            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/code/google")
    public ResponseEntity<?> handleGoogleCode(@RequestBody OAuthCodeRequest request,
                                              HttpServletResponse response) {
        GoogleLoginDTO googleLoginDTO = oAuth2Service
                .handleGoogleLogin(new OAuthCodeDto(request.code(), request.codeVerifier()));

        return switch (googleLoginDTO.getLoginType()) {
            case NEW_USER, EXISTING_USER -> {
                Cookie refreshTokenCookie = cookieUtil
                        .createRefreshTokenCookie(googleLoginDTO.getRefreshToken());

                Cookie accessTokenCookie = cookieUtil
                        .createAccessTokenCookie(googleLoginDTO.getAccessToken());

                response.addCookie(refreshTokenCookie);
                response.addCookie(accessTokenCookie);

                yield ResponseEntity.ok().build();
            }
            case POSSIBLE_LINK -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new OAuth2LinkResponse(
                            googleLoginDTO.getProviderName(),
                            googleLoginDTO.getProviderId(),
                            googleLoginDTO.getUserId()
                    ));
        };
    }
}

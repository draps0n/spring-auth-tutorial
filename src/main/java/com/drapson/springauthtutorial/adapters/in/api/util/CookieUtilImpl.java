package com.drapson.springauthtutorial.adapters.in.api.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtilImpl implements CookieUtil {
    @Value("${spring.tokens.cookie.refresh-token.expires-in}")
    private long refreshTokenExpiry;

    @Value("${spring.tokens.cookie.access-token.expires-in}")
    private long accessTokenExpiry;

    @Value("${server.ssl.enabled}")
    private boolean sslEnabled;

    public Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie("REFRESH-TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTokenExpiry);
        cookie.setSecure(sslEnabled);
        return cookie;
    }

    public Cookie invalidateRefreshTokenCookie() {
        Cookie cookie = new Cookie("REFRESH-TOKEN", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setSecure(sslEnabled);
        return cookie;
    }

    public Cookie createAccessTokenCookie(String token) {
        Cookie cookie = new Cookie("ACCESS-TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) accessTokenExpiry);
        cookie.setSecure(sslEnabled);
        return cookie;
    }

}

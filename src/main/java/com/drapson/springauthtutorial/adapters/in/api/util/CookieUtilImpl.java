package com.drapson.springauthtutorial.adapters.in.api.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtilImpl implements CookieUtil {
    @Value("${spring.tokens.cookie.expires-in}")
    private long refreshTokenExpiry;

    public Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie("REFRESH-TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTokenExpiry);
//        cookie.setSecure(true);
        return cookie;
    }

    public Cookie invalidateCookie() {
        Cookie cookie = new Cookie("REFRESH-TOKEN", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }
}

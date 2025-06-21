package com.drapson.springauthtutorial.application;

import jakarta.servlet.http.Cookie;

public interface CookieUtil {
    Cookie createRefreshTokenCookie(String token);
    Cookie invalidateCookie();
}

package com.drapson.springauthtutorial.adapters.in.api.util;

import jakarta.servlet.http.Cookie;

public interface CookieUtil {
    Cookie createRefreshTokenCookie(String token);
    Cookie invalidateCookie();
}

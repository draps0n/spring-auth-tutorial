package com.drapson.springauthtutorial.adapters.out.jwt;

import com.drapson.springauthtutorial.application.out.TokenProvider;
import com.drapson.springauthtutorial.application.exceptions.AccessTokenExpiredException;
import com.drapson.springauthtutorial.application.exceptions.EmptyAccessTokenException;
import com.drapson.springauthtutorial.application.exceptions.InvalidAccessTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtTokenProvider implements TokenProvider {

    private final SecretKey secretKey;
    private final UserDetailsService userDetailsService;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    public JwtTokenProvider(
            UserDetailsService userDetailsService,
            String secretKey,
            long accessTokenExpirationTime,
            long refreshTokenExpirationTime
    ) {
        this.secretKey = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.userDetailsService = userDetailsService;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    @Override
    public String generateToken(UUID userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (accessTokenExpirationTime * 1000L)))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            throw new InvalidAccessTokenException("Invalid token");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new AccessTokenExpiredException("Token has expired");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            throw new UnsupportedJwtException("Provided token format is not supported");
        } catch (IllegalArgumentException e) {
            throw new EmptyAccessTokenException("Access token is empty");
        }
    }

    @Override
    public Authentication getAuthentication(String token) {
        String email = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }

    @Override
    public long getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }


}

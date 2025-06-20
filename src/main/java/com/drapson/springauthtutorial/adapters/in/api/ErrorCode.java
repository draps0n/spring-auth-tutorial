package com.drapson.springauthtutorial.adapters.in.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public enum ErrorCode {

    VALIDATION_ERROR(1001, "Validation Error", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1002, "Access Denied", HttpStatus.FORBIDDEN),
    EMAIL_LINKED_THROUGH_PROVIDER(1003, "Email is already linked through another provider", HttpStatus.CONFLICT),
    EMAIL_LINKED_THROUGH_LOCAL(1004, "Email is already linked through local account", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(1005, "Invalid Credentials", HttpStatus.UNAUTHORIZED),
    INVALID_LINK_TOKEN(1006, "Invalid Link Token", HttpStatus.BAD_REQUEST),
    INVALID_REGISTRATION_TOKEN(1007, "Invalid Registration Token", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED(1008, "Refresh Token Expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_UNKNOWN(1009, "Refresh Token Unknown", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REVOKED(1010, "Refresh Token Revoked", HttpStatus.UNAUTHORIZED),
    USER_ALREADY_EXISTS(1011, "User Already Exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND(1012, "User Not Found", HttpStatus.NOT_FOUND),
    LINKED_USER_NOT_FOUND(1013, "Linked User Not Found", HttpStatus.NOT_FOUND),
    ACCESS_TOKEN_EXPIRED(1014, "Access Token Expired", HttpStatus.UNAUTHORIZED),
    EMPTY_ACCESS_TOKEN(1015, "Empty Access Token", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN(1016, "Invalid Access Token", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_ACCESS_TOKEN_TYPE(1017, "Unsupported Access Token Type", HttpStatus.BAD_REQUEST),
    ADDITIONAL_REGISTRATION_REQUIRED(1018, "Additional Registration Required", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1019, "Unauthorized", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1020, "Invalid Refresh Token", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR(5000, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String title;
    private final HttpStatus status;

    ErrorCode(int code, String title, HttpStatus status) {
        this.code = code;
        this.title = title;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public ProblemDetail getProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setProperty("errorCode", code);
        problemDetail.setType(URI.create("https://inz-api.com/errors/" + code));
        return problemDetail;
    }
}

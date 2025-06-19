package com.drapson.springauthtutorial.adapters.in.api;

public enum ErrorCode {

    VALIDATION_ERROR(1001, "Validation Error"),
    ACCESS_DENIED(1002, "Access Denied"),
    EMAIL_LINKED_THROUGH_PROVIDER(1003, "Email is already linked through another provider"),
    EMAIL_LINKED_THROUGH_LOCAL(1004, "Email is already linked through local account"),
    INVALID_CREDENTIALS(1005, "Invalid Credentials"),
    INVALID_LINK_TOKEN(1006, "Invalid Link Token"),
    INVALID_REGISTRATION_TOKEN(1007, "Invalid Registration Token"),
    REFRESH_TOKEN_EXPIRED(1008, "Refresh Token Expired"),
    REFRESH_TOKEN_UNKNOWN(1009, "Refresh Token Unknown"),
    REFRESH_TOKEN_REVOKED(1010, "Refresh Token Revoked"),
    USER_ALREADY_EXISTS(1011, "User Already Exists"),
    USER_NOT_FOUND(1012, "User Not Found"),
    LINKED_USER_NOT_FOUND(1013, "Linked User Not Found"),
    ACCESS_TOKEN_EXPIRED(1014, "Access Token Expired"),
    EMPTY_ACCESS_TOKEN(1015, "Empty Access Token"),
    INVALID_ACCESS_TOKEN(1016, "Invalid Access Token"),
    UNSUPPORTED_ACCESS_TOKEN_TYPE(1017, "Unsupported Access Token Type"),
    ADDITIONAL_REGISTRATION_REQUIRED(1019, "Additional Registration Required"),
    INTERNAL_SERVER_ERROR(1018, "Internal Server Error");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

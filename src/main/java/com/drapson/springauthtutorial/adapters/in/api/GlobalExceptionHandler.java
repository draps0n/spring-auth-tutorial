package com.drapson.springauthtutorial.adapters.in.api;

import com.drapson.springauthtutorial.adapters.in.security.AdditionalRegistrationInfoNeededException;
import com.drapson.springauthtutorial.adapters.in.security.EmailLinkedToAnotherAccountWithDifferentProviderException;
import com.drapson.springauthtutorial.application.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, DateTimeParseException.class})
    public ProblemDetail handleValidation(Exception ex) {
        ErrorCode error = ErrorCode.VALIDATION_ERROR;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/validation"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ErrorCode error = ErrorCode.ACCESS_DENIED;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/access-denied"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(EmailLinkedThroughProviderException.class)
    public ProblemDetail handleEmailLinked(EmailLinkedThroughProviderException ex) {
        ErrorCode error = ErrorCode.EMAIL_LINKED_THROUGH_PROVIDER;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/email-linked-through-provider"));
        problem.setProperty("errorCode", error.getCode());
        problem.setProperty("linkToken", ex.getLinkToken());
        return problem;
    }

    @ExceptionHandler(EmailLinkedToAnotherAccountWithDifferentProviderException.class)
    public ProblemDetail handleEmailLinkedLocal(EmailLinkedToAnotherAccountWithDifferentProviderException ex) {
        ErrorCode error = ErrorCode.EMAIL_LINKED_THROUGH_LOCAL;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/email-linked-through-local"));
        problem.setProperty("errorCode", error.getCode());
        problem.setProperty("linkToken", ex.getLinkToken());
        return problem;
    }

    @ExceptionHandler(AdditionalRegistrationInfoNeededException.class)
    public ProblemDetail handleAdditionalRegistrationInfoNeeded(AdditionalRegistrationInfoNeededException ex) {
        ErrorCode error = ErrorCode.ADDITIONAL_REGISTRATION_REQUIRED;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/additional-registration-info-needed"));
        problem.setProperty("errorCode", error.getCode());
        problem.setProperty("registrationToken", ex.getRegistrationToken());
        return problem;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidPassword(InvalidCredentialsException ex) {
        ErrorCode error = ErrorCode.INVALID_CREDENTIALS;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/invalid-password"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(InvalidLinkTokenException.class)
    public ProblemDetail handleInvalidLinkToken(InvalidLinkTokenException ex) {
        ErrorCode error = ErrorCode.INVALID_LINK_TOKEN;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/invalid-link-token"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(InvalidRegistrationTokenException.class)
    public ProblemDetail handleInvalidRegistrationToken(InvalidRegistrationTokenException ex) {
        ErrorCode error = ErrorCode.INVALID_REGISTRATION_TOKEN;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/invalid-registration-token"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ProblemDetail handleRefreshTokenExpired(RefreshTokenExpiredException ex) {
        ErrorCode error = ErrorCode.REFRESH_TOKEN_EXPIRED;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/refresh-token-expired"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(RefreshTokenUnknownException.class)
    public ProblemDetail handleRefreshTokenNotFound(RefreshTokenUnknownException ex) {
        ErrorCode error = ErrorCode.REFRESH_TOKEN_UNKNOWN;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/refresh-token-unknown"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(RefreshTokenRevokedException.class)
    public ProblemDetail handleRefreshTokenRevoked(RefreshTokenRevokedException ex) {
        ErrorCode error = ErrorCode.REFRESH_TOKEN_REVOKED;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/refresh-token-revoked"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorCode error = ErrorCode.USER_ALREADY_EXISTS;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/user-already-exists"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ErrorCode error = ErrorCode.USER_NOT_FOUND;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/user-not-found"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(LinkedUserNotFoundException.class)
    public ProblemDetail handleLinkedUserNotFound(LinkedUserNotFoundException ex) {
        ErrorCode error = ErrorCode.LINKED_USER_NOT_FOUND;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/linked-user-not-found"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(AccessTokenExpiredException.class)
    public ProblemDetail handleAccessTokenExpired(AccessTokenExpiredException ex) {
        ErrorCode error = ErrorCode.ACCESS_TOKEN_EXPIRED;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/access-token-expired"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(EmptyAccessTokenException.class)
    public ProblemDetail handleEmptyAccessToken(EmptyAccessTokenException ex) {
        ErrorCode error = ErrorCode.EMPTY_ACCESS_TOKEN;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/empty-access-token"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ProblemDetail handleInvalidAccessToken(InvalidAccessTokenException ex) {
        ErrorCode error = ErrorCode.INVALID_ACCESS_TOKEN;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/invalid-access-token"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(UnsupportedAccessTokenException.class)
    public ProblemDetail handleUnsupportedAccessTokenType(UnsupportedAccessTokenException ex) {
        ErrorCode error = ErrorCode.UNSUPPORTED_ACCESS_TOKEN_TYPE;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/unsupported-access-token-type"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(RefreshTokenNotProvidedException.class)
    public ProblemDetail handleRefreshTokenNotProvided(RefreshTokenNotProvidedException ex) {
        ErrorCode error = ErrorCode.INVALID_REFRESH_TOKEN;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/invalid-refresh-token"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther() {
        ErrorCode error = ErrorCode.INTERNAL_SERVER_ERROR;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, error.getDefaultMessage());
        problem.setTitle(error.getDefaultMessage());
        problem.setType(URI.create("https://inz-api.com/errors/internal-server-error"));
        problem.setProperty("errorCode", error.getCode());
        return problem;
    }
}

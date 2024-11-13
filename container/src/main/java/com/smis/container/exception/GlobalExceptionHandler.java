package com.smis.container.exception;

import com.smis.common.core.dto.ApiResponse;
import com.smis.common.core.exception.*;
import com.smis.common.core.util.Helpers;
import com.smis.user.domain.exception.UserDomainException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.smis.common.core.util.Helpers.DEFAULT_ERROR_MESSAGE;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String ERROR_MESSAGE_PREFIX = "GlobalExceptionHandler: ";

    private ApiResponse<Void> parseResponse(Exception exception, String customMessage) {
        String message = customMessage != null && !customMessage.isBlank() ? customMessage : exception.getMessage();
        log.error(ERROR_MESSAGE_PREFIX + "{}", message, exception);
        return new ApiResponse<>(message, null);
    }

    @ResponseBody
    @ExceptionHandler(value = {InvalidLoginCredentials.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleException(InvalidLoginCredentials invalidLoginCredentials) {
        return parseResponse(invalidLoginCredentials, null);
    }

    @ResponseBody
    @ExceptionHandler(value = {AccessDenied.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleException(AccessDenied accessDenied) {
        return parseResponse(accessDenied, null);
    }

    @ResponseBody
    @ExceptionHandler(value = {UserDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleException(UserDomainException domainException) {
        return parseResponse(domainException, null);
    }

    @ResponseBody
    @ExceptionHandler(value = {RecordNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleException(RecordNotFound recordNotFound) {
        return parseResponse(recordNotFound, null);
    }

    @ResponseBody
    @ExceptionHandler(value = {RecordUpdateFailed.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleException(RecordUpdateFailed exception) {
        return parseResponse(exception, null);
    }

    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleException(ConstraintViolationException constraintViolationException) {
        return parseResponse(constraintViolationException, Helpers.extractViolationsFromException(constraintViolationException));
    }

    @ResponseBody
    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleException(AccessDeniedException exception) {
        return parseResponse(exception, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {DuplicateRecord.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleException(DuplicateRecord exception) {
        return parseResponse(exception, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {DomainException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(DomainException exception) {
        return parseResponse(exception, DEFAULT_ERROR_MESSAGE);
    }

    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception exception) {
        return parseResponse(exception, DEFAULT_ERROR_MESSAGE);
    }


}

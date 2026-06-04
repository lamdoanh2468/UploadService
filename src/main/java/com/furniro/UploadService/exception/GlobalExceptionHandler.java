package com.furniro.UploadService.exception;

import com.furniro.UploadService.dto.API.AType;
import com.furniro.UploadService.dto.API.ErrorType;
import com.furniro.UploadService.utils.UploadErrorCode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<AType> handleUploadException(UploadException ex) {
        log.error("UploadException caught: {}", ex.getMessage());
        UploadErrorCode code = ex.getErrorCode();

        AType error = ErrorType.builder()
                .code(code.getCode())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.valueOf(code.getCode()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AType> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException caught: {}", ex.getMessage());
        AType error = ErrorType.badRequest(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AType> handleGenericException(Exception ex) {
        log.error("Unexpected exception caught: ", ex);
        AType error = ErrorType.serverError("An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

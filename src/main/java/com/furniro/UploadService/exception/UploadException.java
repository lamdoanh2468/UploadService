package com.furniro.UploadService.exception;

import com.furniro.UploadService.utils.UploadErrorCode;

import lombok.Getter;

@Getter
public class UploadException extends RuntimeException {
    private final UploadErrorCode errorCode;

    public UploadException(UploadErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}

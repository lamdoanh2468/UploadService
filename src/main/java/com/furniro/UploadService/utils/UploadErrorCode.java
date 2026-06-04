package com.furniro.UploadService.utils;

import lombok.Getter;

@Getter
public enum UploadErrorCode {

    UPLOAD_FAILED(400, "Upload failed"),

    UPLOAD_NOT_FOUND(404, "Upload not found"),

    UPLOAD_ALREADY_EXISTS(409, "Upload already exists"),

    UPLOAD_DELETE_FAILED(400, "Upload delete failed"),

    UPLOAD_UPDATE_FAILED(400, "Upload update failed");

    private final int code;
    private final String message;

    UploadErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

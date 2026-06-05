package com.furniro.UploadService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.furniro.UploadService.dto.API.AType;
import com.furniro.UploadService.dto.req.FileUploadReq;
import com.furniro.UploadService.service.UploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping()
    public ResponseEntity<AType> uploadImage(@ModelAttribute FileUploadReq file) {
        return ResponseEntity.ok(uploadService.uploadImage(file));
    }

    @PutMapping()
    public ResponseEntity<AType> updateImage(@ModelAttribute FileUploadReq file) {
        return ResponseEntity.ok(uploadService.updateImage(file));
    }
}

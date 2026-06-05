package com.furniro.UploadService.service;

import com.furniro.UploadService.database.entity.FileUpload;
import com.furniro.UploadService.database.repository.FileUploadRepository;
import com.furniro.UploadService.dto.API.AType;
import com.furniro.UploadService.dto.API.ApiType;
import com.furniro.UploadService.dto.API.ErrorType;
import com.furniro.UploadService.dto.req.FileUploadReq;
import com.furniro.UploadService.dto.res.CloudinaryResponse;
import com.furniro.UploadService.exception.UploadException;
import com.furniro.UploadService.utils.UploadErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadService {

    private final CloudinaryService cloudinaryService;
    private final FileUploadRepository fileUploadRepository;
    private final TransactionTemplate transactionTemplate;

    public AType uploadImage(FileUploadReq file) {
        // 1. check file infomation
        if (file == null || file.getFile() == null || file.getFile().isEmpty()) {
            return ErrorType.badRequest("File is required");
        }

        // 2. Upload file to cloudinary
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(file.getFile());

        // 3. Save file to database
        FileUpload fileUpload = new FileUpload();
        fileUpload.setPublicId(cloudinaryResponse.getPublicId());
        fileUpload.setUrl(cloudinaryResponse.getUrl());
        fileUpload.setUploadedBy(file.getUploadedBy());
        fileUpload.setIsActive(false);

        fileUploadRepository.save(fileUpload);

        // 4. Return result
        return ApiType.success(fileUpload);
    }

    public AType updateImage(FileUploadReq file) {

        // 1. Check file information
        if (file == null || file.getFile() == null || file.getFile().isEmpty()) {
            return ErrorType.badRequest("File is required");
        }

        // 2. Find old record (read operation)
        FileUpload oldFileUpload = fileUploadRepository.findById(file.getOldFileId())
                .orElseThrow(() -> new UploadException(UploadErrorCode.UPLOAD_NOT_FOUND));

        String oldPublicId = oldFileUpload.getPublicId();

        // 3. Upload new file to Cloudinary (slow network call, outside transaction)
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(file.getFile());

        // 4. Save changes to database inside a localized transaction (keeps same ID)
        FileUpload updatedFileUpload = transactionTemplate.execute(status -> {
            FileUpload existing = fileUploadRepository.findById(file.getOldFileId())
                    .orElseThrow(() -> new UploadException(UploadErrorCode.UPLOAD_NOT_FOUND));
            existing.setPublicId(cloudinaryResponse.getPublicId());
            existing.setUrl(cloudinaryResponse.getUrl());
            existing.setUploadedBy(file.getUploadedBy());
            existing.setIsActive(true);
            return fileUploadRepository.save(existing);
        });

        // 5. Delete old file from Cloudinary (slow network call, outside transaction)
        if (oldPublicId != null) {
            cloudinaryService.deleteFile(oldPublicId);
        }

        // 6. Return result
        return ApiType.success(updatedFileUpload);
    }

    public void activeImage(Integer fileID) {
        // 1. check file infomation
        if (fileID == null) {
            log.error("File ID is required !");
            return;
        }

        FileUpload fileUpload = fileUploadRepository
                .findById(fileID)
                .orElseThrow(() -> new UploadException(UploadErrorCode.UPLOAD_NOT_FOUND));

        // 2. Active file
        fileUpload.setIsActive(true);
        fileUploadRepository.save(fileUpload);

        // 3. Return result
        log.info("File activated successfully !");
        return;
    }

    public void deleteImage(Integer fileID) {
        // 1. check file infomation
        if (fileID == null) {
            log.error("File ID is required !");
            return;
        }

        FileUpload fileUpload = fileUploadRepository
                .findById(fileID)
                .orElseThrow(() -> new UploadException(UploadErrorCode.UPLOAD_NOT_FOUND));

        // 2. Delete file from cloudinary
        cloudinaryService.deleteFile(fileUpload.getPublicId());

        // 3. Delete file from database
        fileUploadRepository.deleteById(fileID);

        // 4. Return result
        log.info("File deleted successfully !");
        return;
    }

    @Transactional
    public void autoDeleteImageNotActive() {
        log.info("Running scheduled task to clean up inactive image drafts...");
        // Define draft threshold (e.g., 30 days)
        int daysThreshold = 30;
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysThreshold);

        List<FileUpload> draftFiles = fileUploadRepository.findFileDraft(false, cutoff);
        log.info("Found {} inactive image draft(s) older than {} days", draftFiles.size(), daysThreshold);

        for (FileUpload fileUpload : draftFiles) {
            try {
                // Delete from Cloudinary
                if (fileUpload.getPublicId() != null) {
                    cloudinaryService.deleteFile(fileUpload.getPublicId());
                }
                // Delete from database
                fileUploadRepository.delete(fileUpload);
                log.info("Successfully deleted draft image: {}", fileUpload.getId());
            } catch (Exception e) {
                log.error("Failed to delete draft image with ID: " + fileUpload.getId(), e);
            }
        }
    }
}

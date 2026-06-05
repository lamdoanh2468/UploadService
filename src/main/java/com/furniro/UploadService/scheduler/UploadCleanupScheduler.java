package com.furniro.UploadService.scheduler;

import com.furniro.UploadService.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadCleanupScheduler {

    private final UploadService uploadService;

    /**
     * Scheduled task to clean up inactive image drafts once a day at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void autoDeleteImageNotActive() {
        log.info("Starting automatic cleanup of inactive images...");
        try {
            uploadService.autoDeleteImageNotActive();
            log.info("Automatic cleanup of inactive images completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during automatic cleanup of inactive images", e);
        }
    }
}

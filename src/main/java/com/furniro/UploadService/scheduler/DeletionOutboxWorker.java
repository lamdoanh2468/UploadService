package com.furniro.UploadService.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.furniro.UploadService.database.entity.PendingDeletion;
import com.furniro.UploadService.database.repository.PendingDeletionRepository;
import com.furniro.UploadService.service.CloudinaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeletionOutboxWorker {

    private final PendingDeletionRepository pendingDeletionRepository;
    private final CloudinaryService cloudinaryService;

    @Scheduled(fixedDelay = 60000) // Runs every 60 seconds
    public void processPendingDeletions() {
        log.info("Processing pending Cloudinary deletions...");
        List<PendingDeletion> pendings = pendingDeletionRepository.findByStatusAndRetryCountLessThan("PENDING", 5);
        if (pendings.isEmpty()) {
            return;
        }
        for (PendingDeletion pending : pendings) {
            try {
                boolean success = cloudinaryService.deleteFile(pending.getPublicId());
                if (success) {
                    pendingDeletionRepository.delete(pending);
                    log.info("Successfully deleted publicId {} via outbox", pending.getPublicId());
                } else {
                    pending.setRetryCount(pending.getRetryCount() + 1);
                    if (pending.getRetryCount() >= 5) {
                        pending.setStatus("FAILED");
                    }
                    pendingDeletionRepository.save(pending);
                    log.warn("Cloudinary returned failure for publicId {} (retry: {})", pending.getPublicId(),
                            pending.getRetryCount());
                }
            } catch (Exception e) {
                log.error("Failed to delete publicId {} in outbox worker", pending.getPublicId(), e);
                pending.setRetryCount(pending.getRetryCount() + 1);
                if (pending.getRetryCount() >= 5) {
                    pending.setStatus("FAILED");
                }
                pendingDeletionRepository.save(pending);
            }
        }
    }
}
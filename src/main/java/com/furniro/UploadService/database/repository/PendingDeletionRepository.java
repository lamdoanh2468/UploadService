package com.furniro.UploadService.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.furniro.UploadService.database.entity.PendingDeletion;

@Repository
public interface PendingDeletionRepository extends JpaRepository<PendingDeletion, Integer> {
    List<PendingDeletion> findByStatusAndRetryCountLessThan(String status, Integer maxRetries);
}
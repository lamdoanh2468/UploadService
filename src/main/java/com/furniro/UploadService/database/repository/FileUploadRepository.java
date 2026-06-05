package com.furniro.UploadService.database.repository;

import com.furniro.UploadService.database.entity.FileUpload;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Integer> {
    @Query("""
        SELECT f FROM FileUpload f 
        WHERE f.isActive = :isActive 
        AND f.uploadedAt < :cutoffDate
        """)
    List<FileUpload> findFileDraft(
            @Param("isActive") Boolean isActive,
            @Param("cutoffDate") LocalDateTime cutoffDate
    );
}

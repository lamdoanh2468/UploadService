package com.furniro.UploadService.database.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PendingDeletion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingDeletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String publicId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder.Default
    private Integer retryCount = 0;

    @Builder.Default
    private String status = "PENDING";
}
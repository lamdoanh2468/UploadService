package com.furniro.UploadService.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "FileUpload")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Cloudinary
    private String publicId;

    private String url;

    // UserID
    private String uploadedBy;

    // Date
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    private Boolean isActive;

}

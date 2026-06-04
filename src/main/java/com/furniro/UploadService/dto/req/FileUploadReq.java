package com.furniro.UploadService.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
/*
 * Use this class for update and create
 * For create: oldFileId = 0
 * For update: oldFileId = ID of the file to be replaced
 */
public class FileUploadReq {

    private Integer oldFileId;

    private MultipartFile file;

    // UserID
    private String uploadedBy;
}
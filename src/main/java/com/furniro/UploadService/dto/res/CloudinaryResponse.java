package com.furniro.UploadService.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudinaryResponse {

    private String publicId;

    private String url;
}

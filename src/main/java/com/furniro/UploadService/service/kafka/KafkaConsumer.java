package com.furniro.UploadService.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.furniro.UploadService.service.UploadService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final UploadService uploadService;

    @Transactional
    @KafkaListener(topics = "upload.active",
            groupId = "upload",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenActive(Map<String, Object> event) {

        Integer fileID = event.get("fileID") != null
                ? ((Number) event.get("fileID")).intValue() : null;

        log.info("listenActive: {}", fileID);

        uploadService.activeImage(fileID);
    }

    @Transactional
    @KafkaListener(topics = "upload.delete",
            groupId = "upload",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenDelete(Map<String, Object> event) {

        Integer fileID = event.get("fileID") != null
                ? ((Number) event.get("fileID")).intValue() : null;

        log.info("listenDelete: {}", fileID);

        uploadService.deleteImage(fileID);
    }

}
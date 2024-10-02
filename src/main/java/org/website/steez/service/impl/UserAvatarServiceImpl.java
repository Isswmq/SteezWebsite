package org.website.steez.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.website.steez.exception.ImageUploadException;
import org.website.steez.model.user.UserAvatar;
import org.website.steez.config.MinioConfiguration;
import org.website.steez.service.UserAvatarService;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAvatarServiceImpl implements UserAvatarService {

    private final MinioClient minioClient;
    private final MinioConfiguration minioProperties;

    @Override
    public String upload(UserAvatar avatar) {
        log.debug("Attempting to upload avatar: {}", avatar);

        try {
            createBucket();
        } catch (Exception e) {
            log.error("Failed to create bucket: {}", e.getMessage());
            throw new ImageUploadException("Image upload failed " + e.getMessage());
        }

        MultipartFile file = avatar.getFile();
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            log.warn("Image must have a name: {}", file.getOriginalFilename());
            throw new ImageUploadException("Image must have a name");
        }

        String fileName = generateFileName(file);
        log.debug("Generated file name: {}", fileName);

        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            log.error("Failed to get input stream from file: {}", e.getMessage());
            throw new ImageUploadException("Image upload failed " + e.getMessage());
        }

        saveImage(inputStream, fileName);
        log.debug("Successfully uploaded avatar with file name: {}", fileName);
        return fileName;
    }

    @SneakyThrows
    private void createBucket() {
        log.debug("Checking if bucket {} exists", minioProperties.getBucket());
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());

        if (!found) {
            log.debug("Bucket {} not found. Creating new bucket.", minioProperties.getBucket());
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
            log.debug("Bucket {} created successfully", minioProperties.getBucket());
        } else {
            log.debug("Bucket {} already exists", minioProperties.getBucket());
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        String fileName = UUID.randomUUID() + "." + extension;
        log.debug("Generated file name: {}", fileName);
        return fileName;
    }

    private String getExtension(MultipartFile file) {
        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        log.debug("Extracted file extension: {}", extension);
        return extension;
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName) {
        log.debug("Saving image with file name: {}", fileName);
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.getBucket())
                .object(fileName)
                .build());
        log.debug("Image saved successfully with file name: {}", fileName);
    }
}


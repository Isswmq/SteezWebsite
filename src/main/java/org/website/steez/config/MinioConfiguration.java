package org.website.steez.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfiguration {
    private String bucket;
    private String url;
    private String accessKey;
    private String secretKey;
}

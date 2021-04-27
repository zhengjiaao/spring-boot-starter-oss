package com.dist.zja.oss;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-01-25 9:10
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Configuration
@EnableConfigurationProperties({OSSProperties.class})
@ConditionalOnProperty(name = "dist.oss.config.enabled", matchIfMissing = true)
public class OSSAutoConfig {

    private OSSProperties properties;

    public OSSAutoConfig(OSSProperties OSSProperties) {
        if (OSSProperties.getDefaultBucket() != null) {
            validateBucketName(OSSProperties.getDefaultBucket());
        }
        this.properties = OSSProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public OSSClient ossClient() {
        // Create a OSSClient with the OSS server playground, its access key and secret key.
        OSSClient ossClient = OSSClient.builder()
                .endpoint(properties.getEndpoint(), properties.getPort(), properties.isSecure())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        return ossClient;
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public OSSBucketService ossBucketService(OSSClient ossClient) {
        if (StringUtils.isEmpty(properties.getDefaultBucket())) {
            return new OSSBucketService(ossClient);
        }
        return new OSSBucketService(ossClient, properties.getDefaultBucket());
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public OSSObjectService ossObjectService(OSSClient ossClient) {
        if (StringUtils.isEmpty(properties.getDefaultBucket())) {
            return new OSSObjectService(ossClient);
        }
        return new OSSObjectService(ossClient, properties);
    }


    /**
     * 验证桶名称
     * @param name
     */
    private static void validateBucketName(String name) {
        // Bucket names cannot be no less than 3 and no more than 63 characters long.
        if (name.length() < 3 || name.length() > 63) {
            throw new IllegalArgumentException(name + " : " + "defaultBucket name must be at least 3 and no more than 63 characters long");
        }
        // Successive periods in bucket names are not allowed.
        if (name.contains("..")) {
            String msg = "defaultBucket name cannot contain successive periods.";
            throw new IllegalArgumentException(name + " : " + msg);
        }
        // Bucket names should be dns compatible.
        if (!name.matches("^[a-z0-9][a-z0-9\\.\\-]+[a-z0-9]$")) {
            String msg = "defaultBucket name does not follow Amazon S3 standards.";
            throw new IllegalArgumentException(name + " : " + msg);
        }
    }

}

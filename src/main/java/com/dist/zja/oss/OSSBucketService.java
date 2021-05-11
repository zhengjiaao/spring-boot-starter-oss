package com.dist.zja.oss;

import com.dist.zja.oss.common.annotations.ClassComment;
import com.dist.zja.oss.common.annotations.MethodComment;
import com.dist.zja.oss.common.annotations.Param;
import com.dist.zja.oss.common.enums.PolicyEnum;
import io.oss.*;
import io.oss.messages.Bucket;
import io.oss.messages.Item;
import io.oss.messages.ObjectLockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-01-25 16:38
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@ClassComment(value = "OSS 桶服务-操作存储桶", author = "zhengja")
public class OSSBucketService {

    public static Logger logger = LoggerFactory.getLogger(OSSBucketService.class);

    private static final String BUCKET_PARAM = "${BUCKET_NAME}";

    /**
     * bucket权限-只读
     */
    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-只写
     */
    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:PutObject\",\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-读写
     */
    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";

    private OSSClient ossClient;

    private String defaultBucket;

    public OSSBucketService(OSSClient ossClient) {
        this.ossClient = ossClient;
    }

    public OSSBucketService(OSSClient ossClient, String defaultBucket) {
        this.ossClient = ossClient;
        this.defaultBucket = defaultBucket;
    }

    /**
     * 初始化默认桶
     */
    public void init() {
        if (!StringUtils.isEmpty(defaultBucket)) {
            makeBucket();
            logger.info("defaultBucket: " + defaultBucket);
        }

        logger.info("com.dist.zja.oss.OSSBucketService  Init Success！");
    }

    /**
     * 验证桶名称规则
     * @param name
     */
    private void validateBucketName(String name) {
        validateNotNull(name, "bucket name");

        // Bucket names cannot be no less than 3 and no more than 63 characters long.
        if (name.length() < 3 || name.length() > 63) {
            throw new IllegalArgumentException(
                    name + " : " + "bucket name must be at least 3 and no more than 63 characters long");
        }
        // Successive periods in bucket names are not allowed.
        if (name.contains("..")) {
            String msg =
                    "bucket name cannot contain successive periods. For more information refer "
                            + "http://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html";
            throw new IllegalArgumentException(name + " : " + msg);
        }
        // Bucket names should be dns compatible.
        if (!name.matches("^[a-z0-9][a-z0-9\\.\\-]+[a-z0-9]$")) {
            String msg =
                    "bucket name does not follow Amazon S3 standards. For more information refer "
                            + "http://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html";
            throw new IllegalArgumentException(name + " : " + msg);
        }
    }

    /**
     * 验证是否配置默认桶名称
     * @param arg
     * @param argName
     */
    private void validateNotNull(Object arg, String argName) {
        if (arg == null) {
            throw new IllegalArgumentException(argName + " must not be null,Must be configured dist.oss.config.default-bucket=");
        }
    }

    @MethodComment(function = "指定桶-桶是否存在")
    public boolean bucketExists() throws Exception {
        validateBucketName(defaultBucket);
        return bucketExists(defaultBucket);
    }

    @MethodComment(function = "指定桶-桶是否存在")
    public boolean bucketExists(String bucketName) throws Exception {
        return ossClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    @MethodComment(
            function = "默认桶-创建新的桶",
            description = "使用默认桶 defaultBucket，必须配置 dist.oss.config.default-bucket= ")
    private boolean makeBucket() {
        validateBucketName(defaultBucket);
        return makeBucket(defaultBucket);
    }

    @MethodComment(function = "指定桶-创建新的桶")
    public boolean makeBucket(String bucketName) {
        try {
            boolean isExist = bucketExists(bucketName);
            if (!isExist) {
                ossClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @MethodComment(
            function = "当前用户-所有桶名称")
    public List<String> listBucketNames() throws Exception {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    @MethodComment(
            function = "当前用户-所有桶信息")
    public List<Bucket> listBuckets() throws Exception {
        return ossClient.listBuckets();
    }

    @MethodComment(
            function = "默认桶-桶中的对象名称列表")
    public List<String> listObjectNames() throws Exception {
        validateBucketName(defaultBucket);
        return listObjectNames(defaultBucket);
    }

    @MethodComment(
            function = "指定桶-桶中的对象名称列表",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            })
    public List<String> listObjectNames(String bucketName) throws Exception {
        List<String> listObjectNames = new ArrayList<>();
        Iterable<Result<Item>> myObjects = listObjects(bucketName);
        if (null == myObjects) {
            return null;
        }
        for (Result<Item> result : myObjects) {
            Item item = result.get();
            listObjectNames.add(item.objectName());
        }
        return listObjectNames;
    }

    @MethodComment(
            function = "默认桶-桶中所有对象列表",
            description = "使用默认桶 defaultBucket，必须配置 dist.oss.config.default-bucket= ")
    public Iterable<Result<Item>> listObjects() throws Exception {
        validateBucketName(defaultBucket);
        return listObjects(defaultBucket);
    }

    @MethodComment(
            function = "指定桶-桶中所有对象列表",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            })
    public Iterable<Result<Item>> listObjects(String bucketName) throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return ossClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(null).recursive(true).build());
        }
        return null;
    }

    @MethodComment(
            function = "指定桶-删除桶中所有对象",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            })
    public boolean deleteBucketObjects(String bucketName) throws Exception {
        Iterable<Result<Item>> myObjects = this.listObjects(bucketName);
        for (Result<Item> result : myObjects) {
            Item item = result.get();
            if (item.size() > 0) {
                //删除桶中所有对象
                ossClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
            }
        }
        return true;
    }

    @MethodComment(
            function = "指定桶-删除桶",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            },description = "会先删除桶中所有对象，再删除桶")
    public boolean deleteBucket(String bucketName) throws Exception {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            return false;
        }
        //删除桶中所有对象
        this.deleteBucketObjects(bucketName);
        //删除存储桶，注意，只有存储桶为空时才能删除成功。
        ossClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        return true;
    }

/*    @MethodComment(
            function = "默认桶-删除桶", description = "只能删除空桶")
    public boolean deleteNullBucket() throws Exception {
        return deleteNullBucket(defaultBucket);
    }

    @MethodComment(
            function = "指定桶-删除桶",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            }, description = "只能删除空桶")
    public boolean deleteNullBucket(String bucketName) throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            ossClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);
            if (!flag) {
                return true;
            }
        }
        return false;
    }*/

    @MethodComment(
            function = "默认桶-获取桶策略",
            description = "使用默认桶 defaultBucket，必须配置 dist.oss.config.default-bucket= ")
    public String getBucketPolicy() throws Exception {
        return getBucketPolicy(defaultBucket);
    }

    @MethodComment(
            function = "指定桶-获取桶策略",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            })
    public String getBucketPolicy(String bucketName) throws Exception {
        return ossClient.getBucketPolicy(
                GetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .build());
    }

    @MethodComment(
            function = "默认桶-设置桶策略",
            params = {
                    @Param(name = "BucetPolicyEnum", description = "策略枚举")
            })
    public void setBucketPolicy(PolicyEnum policy) throws Exception {
        setBucketPolicy(defaultBucket, policy);
    }

    @MethodComment(
            function = "指定桶-设置桶策略",
            params = {
                    @Param(name = "bucketName", description = "桶名"),
                    @Param(name = "BucetPolicyEnum", description = "策略枚举")
            })
    public void setBucketPolicy(String bucketName, PolicyEnum policy) throws Exception {
        updataBucketPolicy(bucketName, policy);
    }

    @MethodComment(
            function = "指定桶-获取存储桶中的对象锁定配置",
            params = {
                    @Param(name = "bucketName", description = "桶名称)")
            })
    public ObjectLockConfiguration getObjectLockConfiguration(String bucketName) throws Exception {
        return ossClient.getObjectLockConfiguration(
                GetObjectLockConfigurationArgs.builder()
                        .bucket(bucketName)
                        .build());
    }

    @MethodComment(
            function = "默认桶-删除桶策略",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            })
    public void deleteBucketPolicy() throws Exception {
        ossClient.deleteBucketPolicy(
                DeleteBucketPolicyArgs.builder()
                        .bucket(defaultBucket)
                        .build());
    }

    @MethodComment(
            function = "指定桶-删除桶策略",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            })
    public void deleteBucketPolicy(String bucketName) throws Exception {
        ossClient.deleteBucketPolicy(
                DeleteBucketPolicyArgs.builder().
                        bucket(bucketName)
                        .build());
    }

    @MethodComment(
            function = "默认桶-删除存储桶加密",
            description = "使用默认桶 defaultBucket，必须配置 dist.oss.config.default-bucket= ")
    public boolean deleteBucketEncryption() {
        return deleteBucketEncryption(defaultBucket);
    }

    @MethodComment(
            function = "指定桶-删除存储桶加密",
            params = {
                    @Param(name = "bucketName", description = "桶名")
            }, description = "删除桶中所有对象后再删除桶")
    public boolean deleteBucketEncryption(String bucketName) {
        try {
            ossClient.deleteBucketEncryption(
                    DeleteBucketEncryptionArgs
                            .builder()
                            .bucket(bucketName)
                            .build());
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 更新桶权限策略
     *
     * @param bucketName 桶
     * @param policy 权限
     */
    public void updataBucketPolicy(String bucketName, PolicyEnum policy) throws Exception {
        switch (policy) {
            case READ_ONLY:
                ossClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(READ_ONLY.replace(BUCKET_PARAM, bucketName)).build());
                break;
            case WRITE_ONLY:
                ossClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(WRITE_ONLY.replace(BUCKET_PARAM, bucketName)).build());
                break;
            case READ_WRITE:
                ossClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(READ_WRITE.replace(BUCKET_PARAM, bucketName)).build());
                break;
            case NONE:
                deleteBucketPolicy(bucketName);
                break;
            default:
                break;
        }
    }

}

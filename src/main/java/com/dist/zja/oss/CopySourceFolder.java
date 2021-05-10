package com.dist.zja.oss;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-05-10 10:55
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：拷贝源文件夹
 */
public class CopySourceFolder {

    private String bucketName;
    private String objectFolderName;

    public CopySourceFolder(String bucketName, String objectFolderName) {
        this.bucketName = bucketName;
        this.objectFolderName = objectFolderName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectFolderName() {
        return objectFolderName;
    }

    public void setObjectFolderName(String objectFolderName) {
        this.objectFolderName = objectFolderName;
    }
}

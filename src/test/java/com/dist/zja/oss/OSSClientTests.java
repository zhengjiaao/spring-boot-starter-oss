package com.dist.zja.oss;

import io.oss.*;
import io.oss.errors.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-04-27 13:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public class OSSClientTests {

    OSSClient ossClient; // 默认客户端

    @Before
    public void init() {
        OSSClient client = OSSClient.builder()
                .endpoint("127.0.0.1", 9000, false)
                .credentials("username", "password")
                .build();
        this.ossClient = client;
        System.out.println("init()");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test1() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        //手动创建客户端
        OSSClient client = OSSClient.builder()
                .endpoint("127.0.0.1", 9000, false)
                .credentials("username", "password")
                .build();

        client.makeBucket(MakeBucketArgs.builder().bucket("mybucket1").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        //默认客户端 判断桶
        boolean result = ossClient.bucketExists(BucketExistsArgs.builder().bucket("mybucket").build());
        System.out.println("test2-result："+ result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        InputStream input = new FileInputStream(new File("C:\\a.txt"));
        ossClient.putObject(PutObjectArgs.builder().bucket("mybucket").object("a.txt").stream(input, input.available(), -1).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test4() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        ossClient.downloadObject(DownloadObjectArgs.builder().bucket("mybucket").object("a.txt").filename("C:\\a-1.txt").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test5() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        ossClient.removeObject(RemoveObjectArgs.builder().bucket("mybucket").object("a.txt").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test6() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        ossClient.removeBucket(RemoveBucketArgs.builder().bucket("mybucket").build());
    }

}

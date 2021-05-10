package com.dist.zja.oss;

import io.oss.*;
import io.oss.errors.*;
import io.oss.messages.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        //手动创建客户端
        OSSClient client = OSSClient.builder()
                .endpoint("127.0.0.1", 9000, false)
                .credentials("username", "password")
                .build();
        this.ossClient = client;
        System.out.println("init()");
    }

    @Test
    public void test1() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        ossClient.makeBucket(MakeBucketArgs.builder().bucket("mybucket").build());
    }

    @Test
    public void test2() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        //默认客户端 判断桶
        boolean result = ossClient.bucketExists(BucketExistsArgs.builder().bucket("mybucket").build());
        System.out.println("test2-result："+ result);
    }

    @Test
    public void test3() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        InputStream input = new FileInputStream(new File("C:\\a.txt"));
        ossClient.putObject(PutObjectArgs.builder().bucket("mybucket").object("a.txt").stream(input, input.available(), -1).build());
    }

    @Test
    public void test4() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        ossClient.downloadObject(DownloadObjectArgs.builder().bucket("mybucket").object("a.txt").filename("C:\\a-1.txt").build());
    }

    @Test
    public void test5() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        ossClient.removeObject(RemoveObjectArgs.builder().bucket("mybucket").object("a.txt").build());
    }

    @Test
    public void test6() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        ossClient.removeBucket(RemoveBucketArgs.builder().bucket("mybucket").build());
    }

    //*********标签测试-start**********

    //桶标签
    @Test
    public void test7() throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {
        //设置标签
        Map<String,String> map = new HashMap<>();
        map.put("bucketkey","bucketvalue");
        ossClient.setBucketTags(SetBucketTagsArgs.builder().bucket("mybucket").tags(map).build());
        //获取标签
        Tags tags = ossClient.getBucketTags(GetBucketTagsArgs.builder().bucket("mybucket").build());
        System.out.println(tags.get().toString());
    }

    //对象标签
    @Test
    public void test8() throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {
        //设置标签
        Map<String,String> map = new HashMap<>();
        map.put("txt","txt");
        map.put("a","a");
        ossClient.makeBucket(MakeBucketArgs.builder().bucket("mybucket").build());
        //获取标签
        Tags tags = ossClient.getObjectTags(GetObjectTagsArgs.builder().bucket("mybucket").object("a.txt").build());
        System.out.println(tags.get().toString());
    }



    //*********设置加密测试-start**********

    //设置存储桶加密 未成功
    @Test
    public void test9() throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {
        //设置存储桶加密
        ossClient.setBucketEncryption(SetBucketEncryptionArgs.builder().bucket("mybucket1").config(SseConfiguration.newConfigWithSseKmsRule("123")).build());
/*        //设置存储桶生命周期
        ossClient.setBucketLifecycle();
        //设置存储桶通知
        ossClient.setBucketNotification();
        //设置存储桶复制
        ossClient.setBucketReplication();
        //设置存储桶版本控制
        ossClient.setBucketVersioning();*/
        SseConfiguration bucketEncryption = ossClient.getBucketEncryption(GetBucketEncryptionArgs.builder().bucket("mybucket1").build());
        System.out.println(bucketEncryption.toString());
//        ossClient.deleteBucketEncryption(DeleteBucketEncryptionArgs.builder().bucket("mybucket").build());
    }

    //*********设置对象保留测试-start**********

    //设置对象保留 未成功
    @Test
    public void test10() throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {
        Retention retention = new Retention(RetentionMode.COMPLIANCE, ZonedDateTime.now().plusYears(1));
        ossClient.setObjectRetention(
                SetObjectRetentionArgs.builder()
                        .bucket("mybucket")
                        .object("a.txt")
                        .config(retention)
                        .bypassGovernanceMode(true)
                        .build());

        //获取
        Retention objectRetention = ossClient.getObjectRetention(GetObjectRetentionArgs.builder().bucket("mybucket").object("a.txt").build());
        System.out.println(objectRetention.toString());
    }


    //*********设置桶生命周期测试-start**********

    //桶生命周期 未测试
    @Test
    public void test11() throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {
        //参考阿里：https://help.aliyun.com/document_detail/32017.html?spm=a2c4g.11186623.6.938.181c2e32bVIjMh
        List<LifecycleRule> rules = new LinkedList<>();
        /*rules.add(
                new LifecycleRule(
                        Status.ENABLED,
                        null,
                        null,
                        new RuleFilter("documents/"),
                        "rule1",
                        null,
                        null,
                        new Transition((ZonedDateTime) null, 30, "GLACIER")));*/
        rules.add(
                new LifecycleRule(
                        Status.ENABLED,
                        null,
                        new Expiration((ZonedDateTime) null, 365, null),
                        new RuleFilter("logs/"),
                        "rule2",
                        null,
                        null,
                        null));
        LifecycleConfiguration config = new LifecycleConfiguration(rules);
        //设置
        ossClient.setBucketLifecycle(
                SetBucketLifecycleArgs.builder().bucket("mybucket1").config(config).build());

        //查看生命周期规则
        LifecycleConfiguration bucketLifecycle = ossClient.getBucketLifecycle(
                GetBucketLifecycleArgs.builder().bucket("mybucket1").build());

        //清空生命周期规则
        ossClient.deleteBucketLifecycle(
                DeleteBucketLifecycleArgs.builder().bucket("mybucket1").build());

    }


    //*********设置存储桶通知-start**********

    //设置存储桶通知 未测试
    @Test
    public void test12() throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {

        List<EventType> eventList = new LinkedList<>();
        eventList.add(EventType.OBJECT_CREATED_PUT);
        eventList.add(EventType.OBJECT_CREATED_COPY);

        QueueConfiguration queueConfiguration = new QueueConfiguration();
        queueConfiguration.setQueue("arn:oss:sqs::1:webhook");
        queueConfiguration.setEvents(eventList);
        queueConfiguration.setPrefixRule("images");
        queueConfiguration.setSuffixRule("pg");

        List<QueueConfiguration> queueConfigurationList = new LinkedList<>();
        queueConfigurationList.add(queueConfiguration);

        NotificationConfiguration config = new NotificationConfiguration();
        config.setQueueConfigurationList(queueConfigurationList);

        //设置存储桶通知
        ossClient.setBucketNotification(SetBucketNotificationArgs.builder().bucket("mybucket").config(config).build());
    }

    //*********设置存储桶复制-start**********

    //设置存储桶复制 未测试
    @Test
    public void test13() throws Exception {

        Map<String, String> tags = new HashMap<>();
        tags.put("key1", "value1");
        tags.put("key2", "value2");

        ReplicationRule rule =
                new ReplicationRule(
                        new DeleteMarkerReplication(Status.DISABLED),
                        new ReplicationDestination(
                                null, null, "REPLACE-WITH-ACTUAL-DESTINATION-BUCKET-ARN", null, null, null, null),
                        null,
                        new RuleFilter(new AndOperator("TaxDocs", tags)),
                        "rule1",
                        null,
                        1,
                        null,
                        Status.ENABLED);

        List<ReplicationRule> rules = new LinkedList<>();
        rules.add(rule);

        ReplicationConfiguration config =
                new ReplicationConfiguration("REPLACE-WITH-ACTUAL-ROLE", rules);

        ossClient.setBucketReplication(
                SetBucketReplicationArgs.builder().bucket("mybucket").config(config).build());
    }

}

package com.dist.zja.oss;

import com.dist.zja.oss.common.enums.PolicyEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-04-27 13:41
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public class OSSBucketServiceTests {

    OSSBucketService ossBucketService;

    @Before
    public void init() {
        OSSClient ossClient = OSSClient.builder()
                .endpoint("127.0.0.1", 9000, false)
                .credentials("username", "password")
                .build();
        OSSBucketService bucketService = new OSSBucketService(ossClient);
        this.ossBucketService = bucketService;
        System.out.println("init()");
    }

    @Test
    public void test1() throws Exception {
        //创建桶
        ossBucketService.makeBucket("mybucket");
        //判断桶存在
        System.out.println("bucketExists: " + ossBucketService.bucketExists("mybucket"));
    }

    @Test
    public void test2() throws Exception {
        //设置桶策略 可读
        ossBucketService.setBucketPolicy("mybucket", PolicyEnum.READ_WRITE);
        //查看桶策略
        System.out.println("BucketPolicy: " + ossBucketService.getBucketPolicy("mybucket"));
        //删除桶策略
        ossBucketService.deleteBucketPolicy("mybucket");
    }

    @Test
    public void test3() throws Exception {
        //获取桶中所有对象
        List<String> list = ossBucketService.listObjectNames("mybucket");
        System.out.println("ObjectNames: " + list);
    }

    @Test
    public void test4() throws Exception {
        //删除桶中的所有对象
        System.out.println(ossBucketService.deleteBucketObjects("mybucket"));
    }

    @Test
    public void test5() throws Exception {
        //删除桶 会先删除桶中的所有对象，再删除桶
        System.out.println(ossBucketService.deleteBucket("mybucket"));
    }

}

package com.dist.zja.oss;

import com.dist.zja.oss.common.enums.DownloadModeEnum;
import io.oss.ComposeSource;
import io.oss.CopySource;
import io.oss.StatObjectResponse;
import io.oss.messages.DeleteObject;
import io.oss.messages.Item;
import io.oss.messages.Tags;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
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
public class OSSObjectServiceTests {

    OSSObjectService ossObjectService;

    @Before
    public void init() {
        OSSClient ossClient = OSSClient.builder()
                .endpoint("192.168.1.40", 9000, false)
                .credentials("username", "password")
                .build();
        OSSObjectService objectService = new OSSObjectService(ossClient);
        this.ossObjectService = objectService;
        System.out.println("init()");
    }

    /**
     * 上传对象(文件或文件夹)
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        //上传本地文件  底层实现份分片上传
//        ossObjectService.uploadObjectFile("mybucket", "aaa.txt", "D:\\Temp\\aaa.txt");

        //上传文件夹 底层实现分片  空目录过滤不会上传，空文件可以上传
//        ossObjectService.uploadObjectFolder("mybucket", "aaa", "D:\\Temp\\aaa");
//        ossObjectService.uploadObjectFolder("mybucket1", "documents", "D:\\Temp\\documents");

        //上传文件流 底层实现分片
//        InputStream input = new FileInputStream(new File("C:\\a.zip"));
//        ossObjectService.putObject("mybucket", "a.zip",input);

        //获取url链接地址，通过此url put方式上传文件
        String shareLink = ossObjectService.getObjectUploadShareLink("mybucket", "e.txt", 120);
        System.out.println(shareLink);

    }

    @Test
    public void test2() throws Exception {
        //获取对象信息和元数据
        //可作为判断对象是都存在，不存在则抛异常
        StatObjectResponse statObject = ossObjectService.statObject("mybucket", "a.txt");
        System.out.println("test2-statObject：" + statObject);
    }

    @Test
    public void test3() throws Exception {
        //获取永久访问地址
        //前提是需要桶开启 可读或可下载权限
        String url = ossObjectService.getObjectURL("mybucket", "a.txt");
        System.out.println("test2-url：" + url);
    }

    @Test
    public void test4() throws Exception {
        //生成分享链接
        String shareLink = ossObjectService.getObjectDownloadShareLink("mybucket", "c.txt", 120);
        System.out.println("test3-shareLink：" + shareLink);
    }

    @Test
    public void test5() throws Exception {
        //生成二维码 base64
        String qRcode = ossObjectService.getObjectShareQRcode("mybucket", "a.txt", 60);
        System.out.println("test4-qRcode：" + qRcode);
    }

    @Test
    public void test6() throws Exception {
        //下载对象
        ossObjectService.downloadObject("mybucket", "a.txt", "D:\\a.txt");
    }

    @Test
    public void test7() throws Exception {
        //复制对象
        //从一个对象生成一个新的对象
        //根据源a.txt对象重新生成一个b.txt对象
        ossObjectService.copyObject("mybucket", "c.txt", CopySource.builder()
                .bucket("mybucket").object("a.txt").build());
    }

    @Test
    public void test8() throws Exception {
        //获取对象文件夹
        //获取对象文件夹下的所有文件对象
        List<Item> objectFolder = ossObjectService.getObjectFolder("mybucket", "文件夹");
        for (Item item : objectFolder) {
            System.out.println(item.objectName());
        }
    }

    @Test
    public void test81() throws Exception {
        //复制文件夹
        //复制已有的一个文件夹(递归复制文件对象)
        ossObjectService.copyObjectFolder("mybucket1", "bbb",new CopySourceFolder("mybucket","aaa"));
    }

    @Test
    public void test82() throws Exception {
        //下载文件夹 zip 生成格式文件
        ossObjectService.downloadObjectFolder("mybucket","文件夹", DownloadModeEnum.NONE,"D://cc//aa.zip");

        //不同桶下的对象,合并生成新的zip
        /*OSSClient client = OSSClient.builder()
                .endpoint("192.168.1.40", 9000, false)
                .credentials("username", "password")
                .build();

        Map<String,String> objectAndBucket = new HashMap<>();
        objectAndBucket.put("文件夹/img-3840x2160.jpg","mybucket");
        objectAndBucket.put("db.zip","mybucket2");

        OssUtils.localBatchDownLoadOssFile(client, objectAndBucket, DownloadModeEnum.NONE, "D://cc//bbb.zip");*/
    }

    @Test
    public void test9() throws Exception {
        //组合源对象列表, 服务器上已存在的对象列表再次组合成一个对象
        //例如将 总结报告.zip 在本地分片，然后上传分片文件，再进行合并成一个新的文件对象 "总结报告.zip"

        //上传本地文件
        ossObjectService.uploadObjectFile("mybucket", "0_20971520_总结报告",
                "D:\\aaa\\temp\\58623393b4eb\\0_20971520_总结报告.zip");
        ossObjectService.uploadObjectFile("mybucket", "1_6871120_总结报告",
                "D:\\aaa\\temp\\58623393b4eb\\1_6871120_总结报告.zip");

        //每个源对象都必须大于 5242880=5MB , 如：对象 "0_20971520_总结报告" size 20971520 必须大于 5242880=5MB
        List<ComposeSource> sourceObjectList = new ArrayList<ComposeSource>();
        //按顺序合并
        sourceObjectList.add(
                ComposeSource.builder().bucket("mybucket").object("0_20971520_总结报告").build());
        sourceObjectList.add(
                ComposeSource.builder().bucket("mybucket").object("1_6871120_总结报告").build());

        //合并多个文件对象
        ossObjectService.composeObject("mybucket", "总结报告.zip", sourceObjectList);
    }


    @Test
    public void test10() throws Exception {
        //根据前缀获取对象
        //不递归获取子目录
        List<Item> objectsByPrefix = ossObjectService.getAllObjectsByPrefix("mybucket", "aaa/", false);
        for (Item item : objectsByPrefix) {
            System.out.println("objectName: " + item.objectName() + " , isDir: " + item.isDir());
        }
        System.out.println("test10-1： " + objectsByPrefix);

        //根据前缀获取对象
        //递归获取子目录
        List<Item> objects = ossObjectService.getAllObjectsByPrefix("mybucket", "aaa", true);
        for (Item item : objects) {
            System.out.println("objectName: " + item.objectName() + " , isDir: " + item.isDir());
        }
        System.out.println("test10-2： " + objects);

        //根据前缀获取对象
        //递归获取所有对象
        List<Item> objects3 = ossObjectService.getAllObjectsByPrefix("mybucket", null, true);
        for (Item item : objects3) {
            System.out.println("objectName: " + item.objectName() + " , isDir: " + item.isDir());
        }
        System.out.println("test10-3： " + objects3);
    }

    @Test
    public void test11() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        //设置对象标签
        ossObjectService.setObjectTags("mybucket", "a.txt", Tags.newObjectTags(map));

        //获取对象标签
        Tags objectTags = ossObjectService.getObjectTags("mybucket", "a.txt");
        System.out.println(objectTags.get().toString());

        //删除对象标签
        ossObjectService.deleteObjectTags("mybucket", "a.txt");
    }

    @Test
    public void test12() throws Exception {
        //删除单个对象
        ossObjectService.deleteObject("mybucket", "dd/a/a.txt");
    }

    @Test
    public void test13() throws Exception {
        //删除多个对象
        //无法删除对象是目录的
        List<DeleteObject> objectNames = new ArrayList<>();
        objectNames.add(new DeleteObject("0_20971520_总结报告"));
        objectNames.add(new DeleteObject("1_6871120_总结报告"));
        objectNames.add(new DeleteObject("总结报告.zip"));
        ossObjectService.deleteObjects("mybucket", objectNames);
    }

    @Test
    public void test14() throws Exception {
        //删除文件夹
        //包括删除下面的所有对象
        ossObjectService.deleteObjectFolder("mybucket", "aaa");
    }

}

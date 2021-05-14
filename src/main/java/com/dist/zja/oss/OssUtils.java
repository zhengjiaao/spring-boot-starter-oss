package com.dist.zja.oss;

import com.dist.zja.oss.common.annotations.ClassComment;
import com.dist.zja.oss.common.annotations.MethodComment;
import com.dist.zja.oss.common.annotations.Param;
import com.dist.zja.oss.common.enums.DownloadModeEnum;
import io.oss.GetObjectArgs;
import io.oss.GetObjectResponse;
import io.oss.StatObjectArgs;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-05-14 14:54
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：OSS工具类
 */
@ClassComment(value = "OSS工具类", author = "zhengja")
public class OssUtils {

    @MethodComment(
            function = "远程批量下载OSS文件 并打成zip",
            params = {
                    @Param(name = "ossClient", description = "存储桶里的对象名称"),
                    @Param(name = "objectAndBucket", description = "key=对象 , value=桶"),
                    @Param(name = "modeEnum", description = "下载模式 是否合并下载"),
                    @Param(name = "response", description = "HttpServletResponse 响应")
            },
            description = "多个对象压缩后的zip文件")
    public static void remoteBatchDownLoadOssFile(OSSClient ossClient, Map<String, String> objectAndBucket, DownloadModeEnum modeEnum, HttpServletResponse response) throws IOException {
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;fileName=" + OSSId.Id() + ".zip");
        batchDownLoadOssFile(ossClient, objectAndBucket, modeEnum, response.getOutputStream());
    }


    @MethodComment(
            function = "本地批量下载OSS文件-并打成zip",
            params = {
                    @Param(name = "ossClient", description = "存储桶里的对象名称"),
                    @Param(name = "objectAndBucket", description = "key=对象 , value=桶"),
                    @Param(name = "modeEnum", description = "下载模式 是否合并下载"),
                    @Param(name = "zipFilePath", description = "本地存储zip文件路径 例如: c://test.zip")
            },
            description = "多个对象压缩后的zip文件")
    public static void localBatchDownLoadOssFile(OSSClient ossClient, Map<String, String> objectAndBucket, DownloadModeEnum modeEnum, String zipFilePath) throws IOException {

        if (!zipFilePath.endsWith("zip")){
            throw new IllegalArgumentException("zipFilePath Must start with '.zip' end");
        }

        File file = new File(zipFilePath);
        if (file.exists()){
            file.delete();
        }
        Files.createDirectories(Paths.get(file.getParent()));

        FileOutputStream fileOutputStream = new FileOutputStream(new File(zipFilePath));
        batchDownLoadOssFile(ossClient, objectAndBucket, modeEnum, fileOutputStream);
    }


    @MethodComment(
            function = "批量下载OSS对象文件-并打成zip",
            params = {
                    @Param(name = "ossClient", description = "存储桶里的对象名称"),
                    @Param(name = "objectAndBucket", description = "key=对象 , value=桶"),
                    @Param(name = "modeEnum", description = "下载模式 是否合并下载"),
                    @Param(name = "out", description = "输出流")
            },
            description = "多个对象压缩后的zip文件")
    private static void batchDownLoadOssFile(OSSClient ossClient, Map<String, String> objectAndBucket, DownloadModeEnum modeEnum, OutputStream out) throws FileNotFoundException {
        BufferedInputStream bis = null;
        try {
            ZipOutputStream zos = new ZipOutputStream(out);

            for (Map.Entry<String, String> entry : objectAndBucket.entrySet()) {
                String bucketName = entry.getValue();
                String objectname = entry.getKey();

                //不存在则抛异常
                ossClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectname).build());

                //获取文件流
                GetObjectResponse object = ossClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectname).build());
                byte[] buffs = new byte[1024 * 10];

                String zipFile = objectname;
                if (modeEnum == DownloadModeEnum.MERGE_DOWNLOAD) {
                    zipFile = objectname.substring(objectname.lastIndexOf("/") + 1);
                }

                ZipEntry zipEntry = new ZipEntry(zipFile);
                zos.putNextEntry(zipEntry);
                bis = new BufferedInputStream(object, 1024 * 10);

                int read;
                while ((read = bis.read(buffs, 0, 1024 * 10)) != -1) {
                    zos.write(buffs, 0, read);
                }
            }
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                if (null != bis) {
                    bis.close();
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


/*    @MethodComment(
            function = "批量下载OSS对象文件-并打成zip",
            params = {
                    @Param(name = "ossClient", description = "存储桶里的对象名称"),
                    @Param(name = "bucketName", description = "对象存储位置"),
                    @Param(name = "ObjectNames", description = "对象列表名称,不可重复"),
                    @Param(name = "modeEnum", description = "下载模式 是否合并下载"),
                    @Param(name = "out", description = "输出流")
            },
            description = "多个对象压缩后的zip文件")
    private static void batchDownLoadOssFile(OSSClient ossClient, String bucketName, List<String> ObjectNameList, DownloadModeEnum modeEnum, OutputStream out) throws FileNotFoundException {
        BufferedInputStream bis = null;
        try {
            ZipOutputStream zos = new ZipOutputStream(out);
            for (String fileName : ObjectNameList) {
                //不存在则抛异常
                ossClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(fileName).build());

                //获取文件流
                GetObjectResponse object = ossClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
                byte[] buffs = new byte[1024 * 10];

                String zipFile = fileName;
                if (modeEnum == DownloadModeEnum.MERGE_DOWNLOAD) {
                    zipFile = fileName.substring(fileName.lastIndexOf("/") + 1);
                }

                ZipEntry zipEntry = new ZipEntry(zipFile);
                zos.putNextEntry(zipEntry);
                bis = new BufferedInputStream(object, 1024 * 10);

                int read;
                while ((read = bis.read(buffs, 0, 1024 * 10)) != -1) {
                    zos.write(buffs, 0, read);
                }
            }
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                if (null != bis) {
                    bis.close();
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}

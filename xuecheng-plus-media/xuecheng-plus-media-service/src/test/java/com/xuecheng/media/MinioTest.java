package com.xuecheng.media;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description 测试MinIO 完整功能：桶创建、上传、下载、查询列表、查看元信息、获取url、删除、MD5
 * @author Mr.M
 * @date 2022/9/11 21:24
 * @version 1.0
 */
public class MinioTest {

    static MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.100.128:9001")
            .credentials("minioadmin", "minioadmin123")
            .build();
    //将分块文件上传至minio
    @Test
    public void uploadChunk(){
        String chunkFolderPath = "D:\\develop\\upload\\chunk\\";
        File chunkFolder = new File(chunkFolderPath);
        //分块文件
        File[] files = chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            try {
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket("testbucket").object("chunk/" + i).filename(files[i].getAbsolutePath()).build();
                minioClient.uploadObject(uploadObjectArgs);
                System.out.println("上传分块成功"+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    //合并文件，要求分块文件最小5M
    @Test
    public void test_merge() throws Exception {
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(6)
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunk/".concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());

        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder().bucket("testbucket").object("merge01.mp4").sources(sources).build();
        minioClient.composeObject(composeObjectArgs);

    }
    //清除分块文件
    @Test
    public void test_removeObjects(){
        //合并分块完成将分块文件清除
        List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                .limit(6)
                .map(i -> new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("testbucket").objects(deleteObjects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError = null;
            try {
                deleteError = r.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    // ========== 1. 判断桶是否存在，不存在新建桶 ==========
    @Test
    public void testBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("hirofanother").build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("hirofanother").build());
            System.out.println("桶 hirofanother 创建成功");
        } else {
            System.out.println("桶 hirofanother 已存在");
        }
    }

    // ========== 2. 上传文件（你原来的上传方法） ==========
    @Test
    public void upload() {
        try {
            UploadObjectArgs uploadArgs = UploadObjectArgs.builder()
                    .bucket("hirofan01")
                    .object("001/test001.mp3")//minio内虚拟路径
                    .filename("D:\\information\\musci\\9420 - 麦小兜.mp3") //本地源文件
                    .contentType("audio/mp3")
                    .build();
            minioClient.uploadObject(uploadArgs);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }

    // ========== 3. 下载文件到本地磁盘 ==========
    @Test
    public void download() throws Exception {
        DownloadObjectArgs args = DownloadObjectArgs.builder()
                .bucket("hirofan01")
                .object("001/test001.mp3") //minio里的对象名
                .filename("D:\\IddMissLan\\code\\06xuecheng\\minio\\downtest\\test001.mp3") //下载保存到本地路径
                .build();
        minioClient.downloadObject(args);
        System.out.println("文件下载完成");
    }

    // ========== 4. 查看桶内所有文件（列出对象） ==========
    @Test
    public void listFiles() throws Exception {
        ListObjectsArgs args = ListObjectsArgs.builder()
                .bucket("hirofan01")
                .prefix("001/") //只查看001目录下，不传就是查全部
                .recursive(true) //递归查询子目录
                .build();

        Iterable<Result<Item>> results = minioClient.listObjects(args);
        Iterator<Result<Item>> iterator = results.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next().get();
            System.out.println("文件名:" + item.objectName()
                    + " | 文件大小:" + item.size()
                    + " | 修改时间:" + item.lastModified());
        }
    }

    // ========== 5. 查询单个文件详情（元信息，查看是否存在） ==========
    @Test
    public void getFileStat() throws Exception {
        StatObjectArgs args = StatObjectArgs.builder()
                .bucket("hirofan01")
                .object("001/test001.mp3")
                .build();
        StatObjectResponse stat = minioClient.statObject(args);
        System.out.println("文件大小：" + stat.size());
        System.out.println("contentType：" + stat.contentType());
        System.out.println("ETag(MD5)：" + stat.etag());
    }

    // ========== 6. 获取文件临时访问URL（前端预览、播放用，学成media重点） ==========
    @Test
    public void getPreviewUrl() throws Exception {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket("hirofan01")
                .object("001/test001.mp3")
                .method(Method.GET)
                .expiry(60 * 60) //链接有效期 单位秒，这里1小时
                .build();
        String url = minioClient.getPresignedObjectUrl(args);
        System.out.println("文件临时访问链接：" + url);
    }

    // ========== 7. 删除MinIO里面的文件 ==========
    @Test
    public void deleteFile() throws Exception {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket("hirofan01")
                .object("001/test001.mp3")
                .build();
        minioClient.removeObject(args);
        System.out.println("文件删除成功");
    }

    // ========== MD5工具方法 + MD5测试 ==========
    public String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                md5Digest.update(buffer,0,read);
            }
        }
        byte[] digest = md5Digest.digest();
        StringBuilder sb = new StringBuilder();
        for(byte b : digest){
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() ==1 ) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    @Test
    public void testMd5() throws IOException, NoSuchAlgorithmException {
        File file = new File("D:\\information\\musci\\9420 - 麦小兜.mp3");
        String md5 = getFileMD5(file);
        System.out.println("本地文件MD5值："+md5);
    }
}

package com.atguigu.test;

import org.csource.fastdfs.*;

/**
 * @author ccc
 * @create 2020-08-22 8:42
 */
public class TestFdfs {
    public static void main(String[] args) throws Exception{
        //1、获取fdfs的全局配置信息
        String path = TestFdfs.class.getClassLoader().getResource("tracker.conf").getPath();
        ClientGlobal.init(path);
        //2、获取tracker
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();


        //3、获取storage
        StorageClient storageClient = new StorageClient(connection, null);


        //4、上传
        storageClient.upload_file("","jpg",null);

        //5、返回url
    }
}

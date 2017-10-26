package com.dianxian.storage.service;

import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User         : ethan
 * Date         : 2016/5/6
 * Time         : 17:55
 * Description  : qiniu storage test
 */
public class QiniuFileStorageTest {

    @Test
    public void testSave() throws Exception {
        String key = "X-dXRnRJVLf_pHBp11ku2dyLYZLVjcOXWVKp1fPR";
        String secretKey = "HQfNyRRYFblEelzZGocNmo0o8enEBOQ2igy10sQO";
        String bucketName = "uuuuu";
        String bucketURL = "http://o86e356yw.bkt.clouddn.com";
        QiniuFileStorage qiniuFileStorage = new QiniuFileStorage(key, bucketName, bucketURL, secretKey);
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/test.jpg");
        URL resultUrl = qiniuFileStorage.save(resourceAsStream, "local/storage/qiniu_upload_test.jpg");
        System.out.println(resultUrl);
        //http://7xthb5.com1.z0.glb.clouddn.com/local/storage/qiniu_upload_test.jpg
    }
}
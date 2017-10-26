package com.dianxian.storage.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User         : ethan
 * Date         : 2016/5/6
 * Time         : 17:30
 * Description  : store file to qiu.com
 */
public class QiniuFileStorage {
    protected static Logger logger = LoggerFactory.getLogger(QiniuFileStorage.class);

    private final String accessKey;
    private final String bucketName;
    private final String bucketURL;
    private final String secretKey;
    private Auth auth;
    UploadManager uploadManager;

    public QiniuFileStorage(String accessKey, String bucketName, String bucketURL, String secretKey) {
        this.accessKey = accessKey;
        this.bucketName = bucketName;
        this.bucketURL = bucketURL;
        this.secretKey = secretKey;
        auth = Auth.create(accessKey, secretKey);
        uploadManager = new UploadManager();
    }

    private String getAuthToken(String bucket) {
        return auth.uploadToken(bucket);
    }

    /**
     * save file to qiniu storage and return a url
     *
     * @param file         file
     * @param destFileName file name used as storage key, e.g. common/fancy/abc.png
     * @return url related with bucket and file key
     */
    public URL save(File file, String destFileName) {
        try {
            logger.info(String.format("start upload file [%s] to qiniu storage bucket [%s]...", destFileName, bucketName));
            Response response = uploadManager.put(file, destFileName, getAuthToken(bucketName));
            String url = String.format("%s/%s", bucketURL, destFileName);
            logger.info(String.format("response is: [%s] and url is: [%s]", response.bodyString(), url));
            return new URL(url);
        } catch (QiniuException e) {
            logger.error("erro in save file to qiniu, returns code: " + e.code());
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * save file inputStream to qiniu.
     *
     * @param inputStream  file
     * @param destFileName object key. file name used as storage key, e.g. common/fancy/abc.png
     * @return url related with bucket and file key
     */
    public URL save(InputStream inputStream, String destFileName) {
        try {
            logger.info(String.format("start upload file [%s] to qiniu storage bucket [%s]...", destFileName, bucketName));
            byte[] bytes = IOUtils.toByteArray(inputStream);
            Response response = uploadManager.put(bytes, destFileName, getAuthToken(bucketName));
            String url = String.format("%s/%s", bucketURL, destFileName);
            logger.info(String.format("response is: [%s] and url is: [%s]", response.bodyString(), url));
            return new URL(url);
        } catch (QiniuException e) {
            logger.error("erro in save file to qiniu, returns code: " + e.code());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getBucketURL() {
        return bucketURL;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Auth getAuth() {
        return auth;
    }
}

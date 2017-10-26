package com.dianxian.storage.utils;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.UnsupportedEncodingException;

/**
 * Created by xuwenhao on 2016/7/26.
 */
public class FileUploadUtils {
    public static String getFileName(FormDataContentDisposition contentDisposition) {
        try {
            return new String(contentDisposition.getFileName().getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.dianxian.testframework;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.util.Date;

/**
 * Created by xuwenhao on 2016/7/26.
 */
public class MockFormDataContentDisposition extends FormDataContentDisposition {
    public MockFormDataContentDisposition(String name, String fileName) {
        this("form-data", name, fileName, new Date(), new Date(), new Date(), 0);
    }
    public MockFormDataContentDisposition(String type, String name, String fileName, Date creationDate, Date modificationDate, Date readDate, long size) {
        super(type, name, fileName, creationDate, modificationDate, readDate, size);
    }
}

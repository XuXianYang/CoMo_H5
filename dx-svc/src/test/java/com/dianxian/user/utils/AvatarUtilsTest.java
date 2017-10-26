package com.dianxian.user.utils;

import com.dianxian.testframework.ClassPathResourceLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xuwenhao on 2016/7/27.
 */
public class AvatarUtilsTest {

    public static void main(String[] args) throws IOException {
        InputStream inputStream = ClassPathResourceLoader.getResourceAsStream("index2.png");
        InputStream thumb = AvatarUtils.userAvatarImageTailor(inputStream, 100, 100, 600, 600);
        byte[] bytes = IOUtils.toByteArray(thumb);
        FileUtils.writeByteArrayToFile(new File("t.png"), bytes);
    }
}

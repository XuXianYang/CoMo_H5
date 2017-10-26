package com.dianxian.user.utils;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.user.consts.UserConstants;
import com.dianxian.user.consts.UserServiceCodes;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xuwenhao on 2016/7/23.
 */
public class AvatarUtils {
    public static InputStream userAvatarImageTailor(InputStream originImg,
                                                    int topX, int topY, int width, int height) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(originImg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int srcImageWidth = bufferedImage.getWidth();
        int srcImageHeight = bufferedImage.getHeight();

        if(srcImageWidth  < topX + width || srcImageHeight < topY + height) {
            //输入参数超限
            throw new BizLogicException(UserServiceCodes.AVATAR_CUT_PARAMETER_LIMIT, "AVATAR_CUT_PARAMETER_LIMIT");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //裁剪
        try {
            int sizeWidth = Math.min(width, UserConstants.AVATAR_MAX_WIDTH);
            int sizeHeight = Math.min(width, UserConstants.AVATAR_MAX_HEIGHT);
            Thumbnails.of(bufferedImage).sourceRegion(topX, topY, width, height).size(sizeWidth, sizeHeight).
                    outputFormat("png").toOutputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (outputStream.size() <= 0){
            throw new BizLogicException(UserServiceCodes.AVATAR_CUT_IMAGE_FAIL, "AVATAR_CUT_IMAGE_FAIL");
        }
        //此处会存在内存的问题
        InputStream newInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return newInputStream;
    }
}

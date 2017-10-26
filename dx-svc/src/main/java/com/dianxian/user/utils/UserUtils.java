package com.dianxian.user.utils;

import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.user.consts.UserConstants;

/**
 * Created by xuwenhao on 2016/5/4.
 */
public class UserUtils {
    public static final String CODE_RANDOM_BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String genStudentCode() {
        return StringUtils.getRandomString(CODE_RANDOM_BASE, UserConstants.CODE_LENGTH);
    }
}

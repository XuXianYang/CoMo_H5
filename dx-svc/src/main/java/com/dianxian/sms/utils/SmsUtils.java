package com.dianxian.sms.utils;

import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.sms.SmsConstants;

/**
 * Created by XuWenHao on 4/20/2016.
 */
public class SmsUtils {
    protected static final String SMS_RANDOM_BASE = "0123456789";

    /**
     * 生成随机6位数字的短信验证码
     * @return
     */
    public static String genRandomOtp() {
        return StringUtils.getRandomString(SMS_RANDOM_BASE, SmsConstants.OTP_LENGTH);
    }
}

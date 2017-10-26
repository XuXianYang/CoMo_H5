package com.dianxian.sms.service;

import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.sms.domain.VerifyOtpResult;
import com.dianxian.sms.dto.OneTimePasswordDto;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.testframework.DataUtils;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Created by XuWenHao on 4/19/2016.
 */
public class OtpServiceTest extends AbstractServiceTest {
    @Autowired
    OtpService otpService;

    @BeforeSuite(alwaysRun = true)
    public void setup() {
        org.apache.ibatis.logging.LogFactory.useSlf4jLogging();
    }

    @Test
    public void testSendRegisterOtp() {
//        smsService.sendRegisterOtp("18616392016");
//        otpService.sendRegisterOtp(DataUtils.getRandomMobile());
        otpService.sendRegisterOtp("13916277424");
    }

    @Test
    public void test_sendFindPwdOtp() {
        otpService.sendFindPwdOtp("13916277424");
    }

    @Test
    public void testVerifyOtp() {
        OneTimePasswordDto dto = otpService.sendRegisterOtp(DataUtils.getRandomMobile());
        VerifyOtpResult result = otpService.verifyOtp(dto.getOtpCode(), dto.getMobileNo());
        Assert.assertEquals(result, VerifyOtpResult.SUCCESS);
    }

    @Test
    public void testVerifyOtpFail() {
        OneTimePasswordDto dto = otpService.sendRegisterOtp(StringUtils.getRandomString("0123456789", 11));
        VerifyOtpResult result = otpService.verifyOtp(dto.getOtpCode() + "1", dto.getMobileNo());
        Assert.assertEquals(result, VerifyOtpResult.OTP_NOT_MATCH);
    }

}

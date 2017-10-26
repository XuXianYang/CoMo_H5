package com.dianxian.sms.service;

import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.sms.domain.VerifyOtpResult;
import com.dianxian.sms.dto.OneTimePasswordDto;
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
public class SmsServiceTest extends AbstractServiceTest {
    @Autowired
    SmsService smsService;

    @BeforeSuite(alwaysRun = true)
    public void setup() {
        org.apache.ibatis.logging.LogFactory.useSlf4jLogging();
    }

    @Test
    public void test_sendSafeSms() {
        smsService.sendSafeSms("学生a", "13916277424");
    }

    public static void main(String[] args) throws ApiException {
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23347591", "d52404a1f1fa1f6e2e316e846b140b8f");
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();

        req.setExtend("123456");
        req.setSmsType("normal");
        req.setSmsFreeSignName("大鱼测试1");
        req.setSmsParamString("{\"code\":\"1234\",\"product\":\"alidayu\"}");
        req.setRecNum("13916277424");
        req.setSmsTemplateCode("SMS_7796120");
        AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
        System.out.println(rsp.getErrorCode());
        System.out.println(rsp.getSubCode());
        System.out.println(rsp.getMsg());
        System.out.println(rsp.getSubMsg());
        System.out.println(rsp.getBody());
    }
}

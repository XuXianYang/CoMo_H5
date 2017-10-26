package com.dianxian.sms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by XuWenHao on 4/18/2016.
 */
@Component
public class SmsAppProperties {
    @Value("${sms.isp.url}")
    private String smsIspUrl;
    @Value("${sms.appKey}")
    private String smsAppKey;
    @Value("${sms.appSecretKey}")
    private String smsAppSecretKey;
    @Value("${sms.extend}")
    private String smsExtend;

    @Value("${sms.freeSignName}")
    private String smsFreeSignName;
    @Value("${sms.type}")
    private String smsType;
    @Value("${sms.register.template}")
    private String registerTemplateId;

    @Value("${sms.productName}")
    private String smsProductName;
    @Value("${debug.sms.switch}")
    private Boolean smsSwitch;
    @Value("${debug.otp.verify.switch}")
    private Boolean otpVerifySwitch;

    @Value("${sms.sos.template}")
    private String sosTemplateId;

    @Value("${sms.safe.template}")
    private String safeTemplateId;

    @Value("${sms.security.timeout.template}")
    private String securityTimeoutTemplateId;

    @Value("${sms.find.password.template}")
    private String findPwdTemplateId;

    public String getFindPwdTemplateId() {
        return findPwdTemplateId;
    }

    public void setFindPwdTemplateId(String findPwdTemplateId) {
        this.findPwdTemplateId = findPwdTemplateId;
    }

    public String getSmsIspUrl() {
        return smsIspUrl;
    }

    public String getSmsAppKey() {
        return smsAppKey;
    }

    public String getSmsAppSecretKey() {
        return smsAppSecretKey;
    }

    public String getSmsExtend() {
        return smsExtend;
    }

    public String getSmsFreeSignName() {
        return smsFreeSignName;
    }

    public String getSmsType() {
        return smsType;
    }

    public String getRegisterTemplateId() {
        return registerTemplateId;
    }

    public String getSmsProductName() {
        return smsProductName;
    }

    public Boolean getSmsSwitch() {
        return smsSwitch;
    }

    public Boolean getOtpVerifySwitch() {
        return otpVerifySwitch;
    }

    public String getSosTemplateId() {
        return sosTemplateId;
    }

    public String getSafeTemplateId() {
        return safeTemplateId;
    }

    public String getSecurityTimeoutTemplateId() {
        return securityTimeoutTemplateId;
    }
}

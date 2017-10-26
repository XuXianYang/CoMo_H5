package com.dianxian.sms.domain;

/**
 * 注册时发送短信验证码的参数
 * Created by XuWenHao on 4/20/2016.
 */
public class RegisterOtpSmsParam {
    private String code;
    private String product;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}

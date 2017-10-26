package com.dianxian.sms.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.sms.dao.OneTimePasswordDtoMapper;
import com.dianxian.sms.domain.RegisterOtpSmsParam;
import com.dianxian.sms.domain.VerifyOtpResult;
import com.dianxian.sms.dto.OneTimePasswordDto;
import com.dianxian.sms.dto.OneTimePasswordStatus;
import com.dianxian.sms.dto.SmsDto;
import com.dianxian.sms.dto.SmsStatus;
import com.dianxian.sms.utils.SmsUtils;
import com.taobao.api.domain.BizResult;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * 短信验证码service
 * Created by xuwenhao on 2016/6/19.
 */
@Component
public class OtpService {
    @Autowired
    SmsService smsService;
    @Autowired
    SmsAppProperties smsAppProperties;
    @Autowired
    OneTimePasswordDtoMapper oneTimePasswordDtoMapper;


    /**
     * 注册时发送短信验证码
     * @param mobile
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OneTimePasswordDto sendRegisterOtp(String mobile) {
        validOTPFrequencyLimit(mobile);
        oneTimePasswordDtoMapper.lockOldSms(mobile);

        String otp = SmsUtils.genRandomOtp();
        AlibabaAliqinFcSmsNumSendRequest req = buildRegisterOtpRequest(otp, mobile);
        return sendOtp(req, otp);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public OneTimePasswordDto sendFindPwdOtp(String mobile) {
        validOTPFrequencyLimit(mobile);
        oneTimePasswordDtoMapper.lockOldSms(mobile);

        String otp = SmsUtils.genRandomOtp();
        AlibabaAliqinFcSmsNumSendRequest req = buildFindPwdOtpRequest(otp, mobile);
        return sendOtp(req, otp);
    }
    private void validOTPFrequencyLimit(String moblieNo){
        boolean ret = false;
        Calendar now = Calendar.getInstance();
        Date validTime = new Date(now.getTimeInMillis() - 1000 * 60 * 1);
        int count = oneTimePasswordDtoMapper.countSmsWithTimeLimitByMobile(moblieNo, validTime);
        if (count != 0){
             throw new BizLogicException(ResponseConstants.FREQUENCY_LIMIT, null);
        }
    }
    private OneTimePasswordDto sendOtp(AlibabaAliqinFcSmsNumSendRequest req, String otp){
        OneTimePasswordDto dto = saveOtp(req, otp);

        OneTimePasswordStatus status = OneTimePasswordStatus.SEND_FAIL;
        SmsDto smsDto = smsService.send(req);
        if (SmsStatus.SEND_SUCCESS.getValue() == smsDto.getStatus()) {
            status = OneTimePasswordStatus.SEND_SUCCESS;
        }
        dto.setStatus(status.getValue());
        dto.setSmsId(smsDto.getId());
        oneTimePasswordDtoMapper.updateSmsSendResult(dto);
        return dto;

    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public VerifyOtpResult verifyOtp(String otpCode, String mobileNo) {
        // 开关：是否校验
        if (!smsAppProperties.getOtpVerifySwitch()) {
            return VerifyOtpResult.SUCCESS;
        }
        // 获取未过期的已发送成功的最新一条记录
        OneTimePasswordDto dto = oneTimePasswordDtoMapper.getActiveSmsByMobile(mobileNo);
        if (null == dto) {
            // 找不到匹配的记录，说明已经过期或者已经验证过
            return VerifyOtpResult.OTP_EXPIRED;
        }

        if (!dto.getOtpCode().equals(otpCode)) {
            dto.setStatus(OneTimePasswordStatus.VERIFY_FAIL.getValue());
            oneTimePasswordDtoMapper.updateSmsVerifyResult(dto);
            return VerifyOtpResult.OTP_NOT_MATCH;
        }

        dto.setStatus(OneTimePasswordStatus.VERIFY_SUCCESS.getValue());
        oneTimePasswordDtoMapper.updateSmsVerifyResult(dto);
        return VerifyOtpResult.SUCCESS;
    }

    private OneTimePasswordDto saveOtp(AlibabaAliqinFcSmsNumSendRequest req, String otpCode) {
        OneTimePasswordDto dto = new OneTimePasswordDto();
        dto.setMobileNo(req.getRecNum());
        dto.setOtpCode(otpCode);

        Calendar now = Calendar.getInstance();
        dto.setSendDate(now.getTime());
        dto.setExpireDate(new Date(now.getTimeInMillis() + 1000 * 60 * 5));
        dto.setStatus(OneTimePasswordStatus.INIT.getValue());
        dto.setCreatedAt(Calendar.getInstance().getTime());

        oneTimePasswordDtoMapper.insert(dto);
        return dto;
    }

    private AlibabaAliqinFcSmsNumSendRequest buildRegisterOtpRequest(String otpCode, String mobileNo) {
        AlibabaAliqinFcSmsNumSendRequest req = smsService.buildSmsRequest(mobileNo);
        req.setSmsTemplateCode(smsAppProperties.getRegisterTemplateId());

        RegisterOtpSmsParam param = new RegisterOtpSmsParam();
        param.setCode(otpCode);
        param.setProduct(smsAppProperties.getSmsProductName());
        req.setSmsParamString(JsonUtils.toJson(param));

        return req;
    }

    private AlibabaAliqinFcSmsNumSendRequest buildFindPwdOtpRequest(String otpCode, String mobileNo) {
        AlibabaAliqinFcSmsNumSendRequest req = smsService.buildSmsRequest(mobileNo);
        req.setSmsTemplateCode(smsAppProperties.getFindPwdTemplateId());

        RegisterOtpSmsParam param = new RegisterOtpSmsParam();
        param.setCode(otpCode);
        param.setProduct(smsAppProperties.getSmsProductName());
        req.setSmsParamString(JsonUtils.toJson(param));

        return req;
    }
}

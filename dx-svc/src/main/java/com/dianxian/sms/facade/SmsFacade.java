package com.dianxian.sms.facade;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.sms.SmsServiceCodes;
import com.dianxian.sms.dto.OneTimePasswordDto;
import com.dianxian.sms.dto.OneTimePasswordStatus;
import com.dianxian.sms.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by y on 2016/8/16.
 */
@Component
public class SmsFacade {
    @Autowired
    private OtpService otpService;

    public OneTimePasswordDto sendFindPwdOtp(String mobileNo){
        OneTimePasswordDto result = otpService.sendFindPwdOtp(mobileNo);
        if (OneTimePasswordStatus.SEND_FAIL.getValue() == result.getStatus()) {
            throw new BizLogicException(SmsServiceCodes.OTP_SEND_FAIL, "Fail to sendFindPwdOtp.");
        }
        return result;
    }

    public OneTimePasswordDto sendRegisterOtp(String mobileNo){
        OneTimePasswordDto result = otpService.sendRegisterOtp(mobileNo);
        if (OneTimePasswordStatus.SEND_FAIL.getValue() == result.getStatus()) {
            throw new BizLogicException(SmsServiceCodes.OTP_SEND_FAIL, "Fail to sendRegisterOtp.");
        }
        return result;
    }
}

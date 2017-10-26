package com.dianxian.sms.service;

import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.MessageType;
import com.dianxian.sms.dao.OneTimePasswordDtoMapper;
import com.dianxian.sms.dao.SmsDtoMapper;
import com.dianxian.sms.domain.RegisterOtpSmsParam;
import com.dianxian.sms.domain.VerifyOtpResult;
import com.dianxian.sms.dto.OneTimePasswordDto;
import com.dianxian.sms.dto.OneTimePasswordStatus;
import com.dianxian.sms.dto.SmsDto;
import com.dianxian.sms.dto.SmsStatus;
import com.dianxian.sms.utils.SmsUtils;
import com.google.common.collect.Maps;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.BizResult;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by XuWenHao on 4/18/2016.
 */
@Service
public class SmsService {
    protected static Logger logger = LoggerFactory.getLogger(SmsService.class);

    public static final String SEND_SMS_FAIL_TAG = "[sendSmsFailed]";

    @Autowired
    OneTimePasswordDtoMapper oneTimePasswordDtoMapper;
    @Autowired
    SmsDtoMapper smsDtoMapper;
    @Autowired
    SmsAppProperties smsAppProperties;

    public AlibabaAliqinFcSmsNumSendRequest buildSmsRequest(String mobileNo) {
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();

        req.setExtend(smsAppProperties.getSmsExtend());
        req.setSmsType(smsAppProperties.getSmsType());
        req.setSmsFreeSignName(smsAppProperties.getSmsFreeSignName());
        req.setRecNum(mobileNo);

        return req;
    }

    public SmsDto send(AlibabaAliqinFcSmsNumSendRequest req) {
        AlibabaAliqinFcSmsNumSendResponse sendSmsResponse = null;
        Throwable sendSmsThrowable = null;
        try {
            if (smsAppProperties.getSmsSwitch()) {
                TaobaoClient client = new DefaultTaobaoClient(smsAppProperties.getSmsIspUrl()
                        , smsAppProperties.getSmsAppKey(), smsAppProperties.getSmsAppSecretKey());
                sendSmsResponse = client.execute(req);
            } else {
                // mock结果
                sendSmsResponse = new AlibabaAliqinFcSmsNumSendResponse();
                sendSmsResponse.setResult(new BizResult());
                sendSmsResponse.getResult().setErrCode("0");
            }
        } catch (Throwable t) {
            sendSmsThrowable = t;
            logger.error(SEND_SMS_FAIL_TAG + t.getMessage(), t);
        }
        SmsStatus smsStatus = SmsStatus.SEND_FAIL;
        String response = null;
        if (null != sendSmsThrowable) {
            // 请求发送失败
            response = sendSmsThrowable.getMessage();
        } else {
            // 默认认为服务端返回失败
            if (null != sendSmsResponse.getResult() && "0".equals(sendSmsResponse.getResult().getErrCode())) {
                // code显示发送成功
                smsStatus = SmsStatus.SEND_SUCCESS;
            } else {
                logger.error(SEND_SMS_FAIL_TAG + sendSmsResponse.getBody());
            }
            response = sendSmsResponse.getBody();
        }

        return saveSms(req, response, smsStatus);
    }

    public SmsDto saveSms(AlibabaAliqinFcSmsNumSendRequest req, String response, SmsStatus smsStatus) {
        SmsDto dto = new SmsDto();
        dto.setMobileNo(req.getRecNum());
        dto.setAppKey(smsAppProperties.getSmsAppKey());
        dto.setSmsTemplateId(req.getSmsTemplateCode());
        dto.setSmsRequest(JsonUtils.toJson(req));
        dto.setSmsResponse(response);
        dto.setStatus(smsStatus.getValue());
        dto.setCreatedAt(Calendar.getInstance().getTime());

        smsDtoMapper.insert(dto);
        return dto;
    }

    public SmsDto sendSosSms(String name, String mobileNo, String content) {
        AlibabaAliqinFcSmsNumSendRequest req = buildSmsRequest(mobileNo);
        setSmsParams(req, MessageType.SOS);
        Map<String, String> param = Maps.newHashMap();
        param.put("name", name);
        param.put("content", content);
        req.setSmsParamString(JsonUtils.toJson(param));
        return send(req);
    }
    public SmsDto sendSafeSms(String name, String mobileNo) {
        AlibabaAliqinFcSmsNumSendRequest req = buildSmsRequest(mobileNo);
        setSmsParams(req, MessageType.SAFE);
        Map<String, String> param = Maps.newHashMap();
        param.put("name", name);
        req.setSmsParamString(JsonUtils.toJson(param));
        return send(req);
    }

    public SmsDto sendSecurityTimeoutSms(String name, String mobileNo) {
        AlibabaAliqinFcSmsNumSendRequest req = buildSmsRequest(mobileNo);
        req.setSmsTemplateCode(smsAppProperties.getSecurityTimeoutTemplateId());
        Map<String, String> param = Maps.newHashMap();
        param.put("name", name);
        req.setSmsParamString(JsonUtils.toJson(param));
        return send(req);
    }

    private void setSmsParams(AlibabaAliqinFcSmsNumSendRequest req, int messageType){
        if (messageType == MessageType.SOS){
            req.setSmsTemplateCode(smsAppProperties.getSosTemplateId());
        }
        if (messageType == MessageType.SAFE){
            req.setSmsTemplateCode(smsAppProperties.getSafeTemplateId());
        }
    }
}

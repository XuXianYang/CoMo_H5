package com.dianxian.core.utils.message;

import com.dianxian.core.exception.BizLogicException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * authored by ethan.wang @2016-08-29
 * Description: 用来加载页面展示文案资源
 * 1，resources目录下面创建locale/message.properties文件
 * 2，其中中文内容必须为unicode格式
 */
public final class MessageBundle {
    private final static String BIZ_EXCEPTION_FORMAT = "biz.exception.message.%s";
    private static final String DEFAULT_BIZ_EXCEPTION_MSG_FORMAT = "Code: [%s]. %s";

    private static final Logger log = Logger.getLogger(MessageBundle.class);
    private final static MessageBundle messageBundle = new MessageBundle();

    /**
     * 单例模式
     *
     * @return
     */
    public static MessageBundle instance() {
        return messageBundle;
    }

    private final ResourceBundleMessageSource messageSource;
    private final String DEFAULT_SYSTEM_ERROR_MESSAGE;

    private MessageBundle() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("locale.message");

        DEFAULT_SYSTEM_ERROR_MESSAGE = get("default.system.error.message");
    }

    public String get(String key, Object[] args) {
        return messageSource.getMessage(key, args, null);
    }

    public String get(String key) {
        return get(key, null);
    }

    /**
     * 根据错误代码 error code，获取biz exception展示的消息
     *
     * @param exception 异常
     * @return
     */
    public String formatBizExceptionMessage(BizLogicException exception) {
        if (!StringUtils.isEmpty(exception.getDisplayMessage())) {
            return exception.getDisplayMessage();
        }
        String toReturn;
        try {
            toReturn = formatBizExceptionMessage(exception.getCode());
            if (StringUtils.isEmpty(toReturn)) {
                toReturn = String.format(DEFAULT_BIZ_EXCEPTION_MSG_FORMAT, exception.getCode(), exception.getMessage());
            }
        } catch (Exception e) {
            log.error("error in formatBizException for code:" + exception.getCode());
            toReturn = DEFAULT_SYSTEM_ERROR_MESSAGE;
        }

        return toReturn;
    }

    public String formatBizExceptionMessage(int code) {
        return get(String.format(BIZ_EXCEPTION_FORMAT, code));
    }

    public String formatBizExceptionMessage(int code, String defaultValue) {
        String result = formatBizExceptionMessage(code);
        if (StringUtils.isEmpty(result)) {
            result = defaultValue;
        }
        return result;
    }

    public String getDefaultSystemErrorMessage() {
        return DEFAULT_SYSTEM_ERROR_MESSAGE;
    }

}

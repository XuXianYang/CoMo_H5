package com.dianxian.core.spring.log;

import com.dianxian.core.utils.json.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;


/**
 * 用于记录方法调用的参数和返回结果的Aspect类, 需要配合spring使用
 * @author xuwenhao
 *
 */
public final class InvocationLogAspect {
	private static Logger staticLogger = LoggerFactory.getLogger(InvocationLogAspect.class);
	private boolean useTargetLogger = true;
	
	/**
	 * 是否使用被代理的对象来做为logger的名字. 默认为true
	 * @return
	 */
	public boolean isUseTargetLogger() {
		return useTargetLogger;
	}

	public void setUseTargetLogger(boolean useTargetLogger) {
		this.useTargetLogger = useTargetLogger;
	}

	private Logger getLogger(JoinPoint jp) {
		if (this.useTargetLogger) {
			return LoggerFactory.getLogger(jp.getTarget().getClass());
		}
		return staticLogger;
	}

    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
    	Logger logger = this.getLogger(pjp);
		if (logger.isInfoEnabled()) {
			String paramsStr = "[]";
			if (null != pjp.getArgs() && 0 < pjp.getArgs().length) {
				try {
                    StringBuilder builder = new StringBuilder();
                    builder.append("[");
                    String[] temp = toLogString(pjp.getArgs());
                    builder.append(StringUtils.join(temp, ", "));
                    builder.append("]");

                    paramsStr = builder.toString();
				}
				catch (Throwable e) {
                    logger.warn(e.getMessage());
				}
			}
			
			logger.info("{} params {}", pjp.getSignature().getName(), paramsStr);
		}
        Object retVal = pjp.proceed();
		if (logger.isInfoEnabled()) {
			String resultStr = "";
			try {
				resultStr = JsonUtils.toUnescapedJson(retVal);
			}
			catch (Throwable e) {
				// 转换为Json失败的话，就直接调用toString
				resultStr = (null == retVal) ? "null" : retVal.toString();
			}
			
			logger.info("{} returns {}", pjp.getSignature().getName(), resultStr);
		}
        return retVal;
    }
    
	
	private String[] toLogString(Object[] params) {
		if (null == params || 0 == params.length) {
			return new String[0];
		}
		String[] result = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			try {
                if (params[i] instanceof InputStream) {
                    result[i] = null == params[i] ? "null" : params[i].toString();
                } else {
                    result[i] = JsonUtils.toUnescapedJson(params[i]);
                }
			}
			catch (Throwable e) {
				// 转换为Json失败的话，就直接调用toString
				result[i] = (null == params[i]) ? "null" : params[i].toString();
			}
		}
		
		return result;
	}
}

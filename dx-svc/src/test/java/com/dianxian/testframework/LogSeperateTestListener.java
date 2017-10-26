package com.dianxian.testframework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

/**
 * 在日志中插入一些分隔符，方便定位到一个具体case的日志
 * @author xuwenhao
 *
 */
public class LogSeperateTestListener implements ITestListener, IConfigurationListener, IConfigurationListener2 {
    private static final String TEST_BEGIN = "========== Begin Test: %s ==========";
    private static final String TEST_END = "========== End Test: %s ==========";
    private static final String TEST_SKIPPED = "========== Skip Test: %s ==========";
    private static final String CONFIG_BEGIN = "========== Begin Invoke Configuration: %s ==========";
    private static final String CONFIG_FAIL_BEGIN = "========== Invoke Configuration Failed: %s ==========";
    private static final String CONFIG_END = "========== End Invoke Configuration: %s ==========";
    private static final String CONFIG_INVOKED = "========== End Invoke Configuration: %s ==========";
    private static final String CONFIG_SKIPPED = "========== Skip Configuration: %s ==========";
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    public void onFinish(ITestContext context) {
        // empty
    }

    public void onStart(ITestContext context) {
        // empty
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult testResult) {
        this.logger.info(String.format(TEST_END, getTestName(testResult, true)));
    }

    public void onTestFailure(ITestResult testResult) {
        logDetailCause(testResult.getThrowable());
        this.logger.info(String.format(TEST_END, getTestName(testResult, true)));
    }

    public void onTestSkipped(ITestResult testResult) {
        logDetailCause(testResult.getThrowable());
        this.logger.info(String.format(TEST_SKIPPED, getTestName(testResult, true)));
    }
    
    private void logDetailCause(Throwable exception) {
        if (null == exception) {
            return;
        }
        
        this.logger.error(exception.getMessage(), exception);
    }

    public void onTestStart(ITestResult testResult) {
        this.logger.info(String.format(TEST_BEGIN, getTestName(testResult, false)));
    }

    public void onTestSuccess(ITestResult testResult) {
        this.logger.info(String.format(TEST_END, getTestName(testResult, true)));
    }

    /**
     * 获取Test名字，格式为"TestMethodName"@"CurrentInvocationCount"
     * @param testResult
     * @param isEnd test是否已经运行结束
     * @return
     */
    protected String getTestName(ITestResult testResult, boolean isEnd) {
    	return testResult.getTestClass().getName() + "."
    	+ testResult.getMethod().getMethodName() + (isEnd ? "" : "@" + testResult.getMethod().getCurrentInvocationCount());
    }

    @Override
    public void beforeConfiguration(ITestResult testResult) {
        if (logger.isDebugEnabled()) {
            this.logger.debug(String.format(CONFIG_BEGIN, testResult.getMethod().getMethodName()));
        }
    }

	public void onConfigurationFailure(ITestResult testResult) {
		this.logger.error(String.format(CONFIG_FAIL_BEGIN, testResult.getMethod().getMethodName()));
		logDetailCause(testResult.getThrowable());
		this.logger.error(String.format(CONFIG_END, testResult.getMethod().getMethodName()));
	}

	public void onConfigurationSkip(ITestResult testResult) {
		this.logger.warn(String.format(CONFIG_SKIPPED, testResult.getMethod().getMethodName()));
	}

	public void onConfigurationSuccess(ITestResult testResult) {
        if (logger.isDebugEnabled()) {
            this.logger.debug(String.format(CONFIG_INVOKED, testResult.getMethod().getMethodName()));
        }
	}

}

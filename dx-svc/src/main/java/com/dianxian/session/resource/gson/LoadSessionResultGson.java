package com.dianxian.session.resource.gson;

public class LoadSessionResultGson extends ResultGson {

    private String userId;

    private String loginConsole;
    private String enterpriseFlag;


    public LoadSessionResultGson(String resultId, String resultMsg) {
        super(resultId, resultMsg);
    }

    public LoadSessionResultGson(String resultId, String resultMsg, String userId) {
        super(resultId, resultMsg);
        this.userId = userId;;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginConsole() {
        return loginConsole;
    }

    public void setLoginConsole(String loginConsole) {
        this.loginConsole = loginConsole;
    }

    public String getEnterpriseFlag() {
        return enterpriseFlag;
    }

    public void setEnterpriseFlag(String enterpriseFlag) {
        this.enterpriseFlag = enterpriseFlag;
    }


}

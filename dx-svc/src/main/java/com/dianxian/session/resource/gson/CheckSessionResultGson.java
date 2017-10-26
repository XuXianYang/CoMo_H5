package com.dianxian.session.resource.gson;

public class CheckSessionResultGson extends ResultGson {

    private String userId;
    private String partyNo;
    private String loginConsole;
    private String enterpriseFlag;

    public CheckSessionResultGson(String resultId, String resultMsg) {
        super(resultId, resultMsg);
    }

    public CheckSessionResultGson(String resultId, String resultMsg, String userId) {
        super(resultId, resultMsg);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPartyNo() {
        return partyNo;
    }

    public void setPartyNo(String partyNo) {
        this.partyNo = partyNo;
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

package com.dianxian.session.resource.gson;

public class KeepSessionResultGson extends ResultGson {

    private String sessionId;

    public KeepSessionResultGson(String resultId, String resultMsg) {
        super(resultId, resultMsg);
    }

    public KeepSessionResultGson(String resultId, String resultMsg, String sessionId) {
        super(resultId, resultMsg);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

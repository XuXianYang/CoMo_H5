package com.dianxian.session.resource.gson;

/**
 * Created by y on 2016/5/5.
 */
public class CreateSessionResultGson extends ResultGson{
    private String sessionId;

    public CreateSessionResultGson(String resultId, String resultMsg) {
        super(resultId, resultMsg);
    }

    public CreateSessionResultGson(String resultId, String resultMsg, String sessionId) {
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

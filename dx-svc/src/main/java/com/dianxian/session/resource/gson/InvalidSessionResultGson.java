package com.dianxian.session.resource.gson;

public class InvalidSessionResultGson extends ResultGson {

    private String count;

    public InvalidSessionResultGson(String resultId, String resultMsg) {
        super(resultId, resultMsg);
    }

    public InvalidSessionResultGson(String resultId, String resultMsg, String count) {
        super(resultId, resultMsg);
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

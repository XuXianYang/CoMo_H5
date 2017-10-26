package com.dianxian.session.resource.gson;

public class ResultGson extends BaseGson {

    protected String resultId = "00";
    protected String resultMsg = "成功";

    public ResultGson(String resultId, String resultMsg) {
        this.resultId = resultId;
        this.resultMsg = resultMsg;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

}

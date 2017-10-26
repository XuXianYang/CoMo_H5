package com.dianxian.user.filter;

/**
 * Created by y on 2016/6/22.
 */
public class UserContext {
    private Long userId;
    private int userType = -1;
    private Long loginTime = System.currentTimeMillis();
    private String sessionStatus = SessionStatus.LOGIN;//登录状态 00:登录成功 01：session不存在 02:session已退出 03:session已过期 04:已在他处登录 05:无效的session 07:参数错误 99:未知原因

    //only used by session store in this package
    private String sessionToken;

    private UserContext(){}

    public UserContext(Long userId) {
        this.userId = userId;
    }

    public UserContext(Long userId, String sessionStatus) {
        this.userId = userId;
        this.sessionStatus = sessionStatus;
    }

    public Long getUserId() {
        return userId;
    }
    private void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    private void setUserType(int userType) {
        this.userType = userType;
    }

    public Long getLoginTime() {
        return loginTime;
    }
    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }
    public String getSessionStatus() {
        return sessionStatus;
    }
    public void setSessionStatus(String status) {
        this.sessionStatus = status;
    }

    protected String getSessionToken() {
        return sessionToken;
    }

    protected void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public final static class SessionStatus {
        public final static String LOGIN = "00";
        public final static String NOT_EXIST = "01";
        public final static String LOGOUT = "02";
        public final static String TIMEOUT = "03";
        public final static String LOGIN_SOMEWHERE = "04";
        public final static String INVALID = "05";

        public final static String EMPTY_USER_ID_AND_PARTY_NO = "06";

        public final static String PARAM_UNCORRECT = "07";

        public final static String EXPIRY = "08";
        public final static String WRONG_ENTERPRISE_FLAG = "09";
        public final static String WRONG_ACCESS_LEVEL = "10";

        public final static String OTHER = "99";
    }

    public static UserContextBuilder create() {
        return new UserContextBuilder();
    }

    public static class UserContextBuilder{
        private Long userId;
        private int userType;
        private Long loginTime;
        private String sessionStatus;
        private String sessionToken;

        public UserContextBuilder addUserId(Long userId){
            this.userId = userId;
            return this;
        }

        public UserContextBuilder addUserType(int userType){
            this.userType = userType;
            return this;
        }

        public UserContextBuilder addLoginTime(long loginTime){
            this.loginTime = loginTime;
            return this;
        }

        public UserContextBuilder addSessionStatus(String sessionStatus){
            this.sessionStatus = sessionStatus;
            return this;
        }

        public UserContextBuilder addSessionToken(String sessionToken){
            this.sessionToken = sessionToken;
            return this;
        }


        public UserContext build(){
            UserContext context = new UserContext();
            context.setUserId(this.userId);
            context.setUserType(this.userType);
            context.setLoginTime(this.loginTime);
            context.setSessionStatus(this.sessionStatus);
            context.setSessionToken(this.sessionToken);
            return context;
        }
    }
}

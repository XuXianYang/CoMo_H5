package com.dianxian.session.dto;

import com.dianxian.session.definition.ValidStatus;

import javax.validation.Valid;
import java.util.Date;

public class SessionDTO extends BaseDTO {


    private static final long serialVersionUID = -7406387008674744281L;
    private Long id;
    private String sessionId;
    private Long userId;
    private int userType;
    private Date startTime;
    private Date accessTime;
    private Date endTime;
    //是否有效 0无效 1有效
    private String isValid;
    //失效原因 0正常退出 1超时 2IP地址变更 3单终端登录控制 4疑似攻击 5黑名单
    private String invalidReason;

	//去重令牌：isValid=1时uniqueToken=userId+"_"+loginConsole; isValid=0时uniqueToken=null.
	private String uniqueToken;

    private Date createdAt;


    public SessionDTO() {
    }

    public SessionDTO(String sessionId, Long userId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.isValid= ValidStatus.VALID.key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }

	public String getUniqueToken() {
		return uniqueToken;
	}

	public void setUniqueToken(String uniqueToken) {
		this.uniqueToken = uniqueToken;
	}

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

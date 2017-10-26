package com.dianxian.user.filter;

public final class JerseyConfig {

	private String domainCookiePath = "/";
	private String domainCookieDomain = ".dianxian.com";
	private String domainCookieMaxAge = "300";
	
	private String globalCookieName = "_g";
	private String globalCookieDomain = ".dianxian.com";
	private String globalCookiePath = "/";
	private String globalCookieMaxAge = "94608000";
	
	
	
	private String mdcKeyOfGlobal = "UUID";
	private String mdcKeyOfUserId = "UID";
	private String mdcKeyOfRequestId = "REQUEST_ID";
	
	private String serverType = "WEB";
	private String applicationSalt = ":p2p-354188031195030:";

	
	/**
	 * add by zhouchengming 
	 * 2013.2.5
	 */
	private String sessionServerSwitch;// 开关文件，用于存放系统配置开关，true:启用新登录方案；false：老登录方案
	private String userHostUrl = "https://user.lufax.com/user";// user的host地址，登录页面重定向需要使用
	private String lufaxWebSiteUrl = "http://www.lufax.com"; // lufax首页地址，默认值，可以被修改
	
	public Boolean isWebServer() {
		return "WEB".equalsIgnoreCase(serverType);
	}
	public Boolean isAppServer() {
		return "APP".equalsIgnoreCase(serverType);
	}
	
//	public String getDomainCookieName() {
//		return domainCookieName;
//	}
//	public void setDomainCookieName(String domainCookieName) {
//		this.domainCookieName = domainCookieName;
//	}
	public String getDomainCookiePath() {
		return domainCookiePath;
	}
	public void setDomainCookiePath(String domainCookiePath) {
		this.domainCookiePath = domainCookiePath;
	}
	public String getDomainCookieDomain() {
		return domainCookieDomain;
	}
	public void setDomainCookieDomain(String domainCookieDomain) {
		this.domainCookieDomain = domainCookieDomain;
	}
	public String getDomainCookieMaxAge() {
		return domainCookieMaxAge;
	}
	public void setDomainCookieMaxAge(String domainCookieMaxAge) {
		this.domainCookieMaxAge = domainCookieMaxAge;
	}
	public String getGlobalCookieName() {
		return globalCookieName;
	}
	public void setGlobalCookieName(String globalCookieName) {
		this.globalCookieName = globalCookieName;
	}
	public String getGlobalCookieDomain() {
		return globalCookieDomain;
	}
	public void setGlobalCookieDomain(String globalCookieDomain) {
		this.globalCookieDomain = globalCookieDomain;
	}
	public String getGlobalCookiePath() {
		return globalCookiePath;
	}
	public void setGlobalCookiePath(String globalCookiePath) {
		this.globalCookiePath = globalCookiePath;
	}
	public String getGlobalCookieMaxAge() {
		return globalCookieMaxAge;
	}
	public void setGlobalCookieMaxAge(String globalCookieMaxAge) {
		this.globalCookieMaxAge = globalCookieMaxAge;
	}
	public String getMdcKeyOfGlobal() {
		return mdcKeyOfGlobal;
	}
	public void setMdcKeyOfGlobal(String mdcKeyOfGlobal) {
		this.mdcKeyOfGlobal = mdcKeyOfGlobal;
	}
	public String getMdcKeyOfUserId() {
		return mdcKeyOfUserId;
	}
	public void setMdcKeyOfUserId(String mdcKeyOfUserId) {
		this.mdcKeyOfUserId = mdcKeyOfUserId;
	}
	public String getMdcKeyOfRequestId() {
		return mdcKeyOfRequestId;
	}
	public void setMdcKeyOfRequestId(String mdcKeyOfRequestId) {
		this.mdcKeyOfRequestId = mdcKeyOfRequestId;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getApplicationSalt() {
		return applicationSalt;
	}
	public void setApplicationSalt(String applicationSalt) {
		this.applicationSalt = applicationSalt;
	}
	
	
	public String getSessionServerSwitch() {
		return sessionServerSwitch;
	}
	public void setSessionServerSwitch(String sessionServerSwitch) {
		this.sessionServerSwitch = sessionServerSwitch;
	}
	public String getUserHostUrl() {
		return userHostUrl;
	}
	public void setUserHostUrl(String userHostUrl) {
		this.userHostUrl = userHostUrl;
	}
	public String getLufaxWebSiteUrl() {
		return lufaxWebSiteUrl;
	}
	public void setLufaxWebSiteUrl(String lufaxWebSiteUrl) {
		this.lufaxWebSiteUrl = lufaxWebSiteUrl;
	}
}
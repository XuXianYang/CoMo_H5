package com.dianxian.redis;

public class CacheException extends Exception {

	private static final long serialVersionUID = 1L;

	public CacheException(String msg) {
		super(msg);
	}

	public CacheException(String message, Throwable cause) {
		super(message, cause);
	}

}

package com.dianxian.redis;

@SuppressWarnings("serial")
public class InitializationException extends RuntimeException {

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializationException(String message) {
		super(message);
	}

}

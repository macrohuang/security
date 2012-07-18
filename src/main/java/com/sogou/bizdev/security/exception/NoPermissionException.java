package com.sogou.bizdev.security.exception;

public class NoPermissionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9184257240401283258L;

	public NoPermissionException() {
		super();
	}

	public NoPermissionException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoPermissionException(String message) {
		super(message);
	}

	public NoPermissionException(Throwable cause) {
		super(cause);
	}
}

package org.opensrp.exception;

public class UnallowedIdentifierException extends RuntimeException {

	public UnallowedIdentifierException() {
	}

	public UnallowedIdentifierException(String message) {
		super(message);
	}

	public UnallowedIdentifierException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnallowedIdentifierException(Throwable cause) {
		super(cause);
	}
}

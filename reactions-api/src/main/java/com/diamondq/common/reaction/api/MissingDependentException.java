package com.diamondq.common.reaction.api;

public class MissingDependentException extends RuntimeException {

	private static final long serialVersionUID = -9190684963559952183L;

	public MissingDependentException() {
		super();
	}

	public MissingDependentException(String pMessage, Throwable pCause, boolean pEnableSuppression,
		boolean pWritableStackTrace) {
		super(pMessage, pCause, pEnableSuppression, pWritableStackTrace);
	}

	public MissingDependentException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

	public MissingDependentException(String pMessage) {
		super(pMessage);
	}

	public MissingDependentException(Throwable pCause) {
		super(pCause);
	}

}

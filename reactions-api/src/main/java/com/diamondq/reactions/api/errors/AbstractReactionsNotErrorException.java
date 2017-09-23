package com.diamondq.reactions.api.errors;

import org.checkerframework.checker.nullness.qual.Nullable;

public class AbstractReactionsNotErrorException extends AbstractReactionsException {

	private static final long serialVersionUID = 479432212495314720L;

	public AbstractReactionsNotErrorException(String pMessage) {
		super(pMessage, null);
	}

	public AbstractReactionsNotErrorException(String pMessage, @Nullable Throwable pThrowable) {
		super(pMessage, pThrowable);
	}

}

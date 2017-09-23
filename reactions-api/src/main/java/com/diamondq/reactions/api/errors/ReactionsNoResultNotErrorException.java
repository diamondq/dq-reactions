package com.diamondq.reactions.api.errors;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ReactionsNoResultNotErrorException extends AbstractReactionsNotErrorException {

	private static final long serialVersionUID = 7117367170029531531L;

	public ReactionsNoResultNotErrorException(String pMessage) {
		super(pMessage);
	}

	public ReactionsNoResultNotErrorException(String pMessage, @Nullable Throwable pThrowable) {
		super(pMessage, pThrowable);
	}
}

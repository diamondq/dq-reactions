package com.diamondq.reactions.api.errors;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ReactionsMissingDependentException extends AbstractReactionsException {

	private static final long serialVersionUID = -9190684963559952183L;

	public ReactionsMissingDependentException(String pMessage, @Nullable Throwable pJobError) {
		super(pMessage, pJobError);
	}

}

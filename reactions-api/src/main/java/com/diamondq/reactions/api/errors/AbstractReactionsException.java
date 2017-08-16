package com.diamondq.reactions.api.errors;

import org.checkerframework.checker.nullness.qual.Nullable;

public class AbstractReactionsException extends RuntimeException {

	private static final long			serialVersionUID	= 5420393786848095385L;

	private volatile @Nullable String	mMessagePrefix;

	public AbstractReactionsException(String pMessage, @Nullable Throwable pJobError) {
		super(pMessage, pJobError);
	}

	/**
	 * Sets the message prefix. Used by the Reactions Engine to set information about the currently executing job to
	 * make it easier to debug exceptions.
	 * 
	 * @param pPrefix
	 */
	public void setMessagePrefix(String pPrefix) {
		mMessagePrefix = pPrefix;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public @Nullable String getMessage() {
		String suffix = super.getMessage();
		if (mMessagePrefix == null)
			return suffix;
		return mMessagePrefix + " " + suffix;
	}
}

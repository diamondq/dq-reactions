package com.diamondq.common.reaction.api.impl;

import com.diamondq.common.reaction.api.Action;
import com.diamondq.common.reaction.api.JobBuilder;
import com.diamondq.common.reaction.api.TriggerBuilder;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TriggerBuilderImpl<TT> extends CommonBuilderImpl<TT, TriggerBuilder<TT>> implements TriggerBuilder<TT> {

	private @Nullable Action	mAction;

	private boolean				mIsCollection;

	public TriggerBuilderImpl(JobBuilderImpl pJobSetup, boolean pIsCollection, Class<TT> pClass) {
		super(pJobSetup, pClass);
		mIsCollection = pIsCollection;
	}

	@Override
	public TriggerBuilderImpl<TT> action(Action pAction) {
		mAction = pAction;
		return this;
	}

	/**
	 * Finish this trigger and return back to the job
	 * 
	 * @return the job builder
	 */
	@Override
	public JobBuilder build() {
		mJobSetup.addTrigger(this);
		return mJobSetup;
	}

	public Action getAction() {
		Action action = mAction;
		if (action == null)
			throw new IllegalArgumentException("The action is required");
		return action;
	}

	public boolean isCollection() {
		return mIsCollection;
	}
}

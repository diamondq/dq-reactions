package com.diamondq.reactions.api.impl;

import com.diamondq.reactions.api.JobBuilder;
import com.diamondq.reactions.api.ResultBuilder;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ResultBuilderImpl<RT> extends CommonResultBuilderImpl<RT, ResultBuilder<RT>> implements ResultBuilder<RT> {

	private @Nullable String mNameByVariable;

	public ResultBuilderImpl(JobBuilderImpl pJobSetup, Class<RT> pClass) {
		super(pJobSetup, pClass);
	}

	/**
	 * @see com.diamondq.reactions.api.ResultBuilder#nameByVariable(java.lang.String)
	 */
	@Override
	public ResultBuilder<RT> nameByVariable(String pVariableName) {
		mNameByVariable = pVariableName;
		return this;
	}

	public @Nullable String getNameByVariable() {
		return mNameByVariable;
	}

	/**
	 * Finish this param and return back to the job
	 * 
	 * @return the job builder
	 */
	@Override
	public JobBuilder build() {
		mJobSetup.addResult(this);
		return mJobSetup;
	}
}

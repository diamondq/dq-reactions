package com.diamondq.common.reaction.api.impl;

import com.diamondq.common.reaction.api.CommonResultBuilder;

public class CommonResultBuilderImpl<RT, AS extends CommonResultBuilder<RT, AS>> extends CommonBuilderImpl<RT, AS>
	implements CommonResultBuilder<RT, AS> {

	protected boolean mResultIsParam = false;

	protected CommonResultBuilderImpl(JobBuilderImpl pJobSetup, Class<RT> pClass) {
		super(pJobSetup, pClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public AS asParam() {
		mResultIsParam = true;
		return (AS) this;
	}

	public boolean getResultIsParam() {
		return mResultIsParam;
	}
}

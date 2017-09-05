package com.diamondq.reactions.api.impl;

import com.diamondq.reactions.api.JobBuilder;
import com.diamondq.reactions.api.ResultBuilder;

public class ResultBuilderImpl<RT> extends CommonResultBuilderImpl<RT, ResultBuilder<RT>> implements ResultBuilder<RT> {

	public ResultBuilderImpl(JobBuilderImpl pJobSetup, Class<RT> pClass) {
		super(pJobSetup, pClass);
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

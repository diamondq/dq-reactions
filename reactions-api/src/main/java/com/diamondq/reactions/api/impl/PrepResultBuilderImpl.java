package com.diamondq.reactions.api.impl;

import com.diamondq.reactions.api.JobBuilder;
import com.diamondq.reactions.api.PrepResultBuilder;

public class PrepResultBuilderImpl<RT> extends CommonResultBuilderImpl<RT, PrepResultBuilder<RT>>
	implements PrepResultBuilder<RT> {

	public PrepResultBuilderImpl(JobBuilderImpl pJobSetup, Class<RT> pClass) {
		super(pJobSetup, pClass);
	}

	/**
	 * @see com.diamondq.reactions.api.PrepResultBuilder#stateByVariable(java.lang.String)
	 */
	@Override
	public PrepResultBuilderImpl<RT> stateByVariable(String pVariable) {
		mRequiredStates.add(new StateVariableCriteria(pVariable, true));
		return this;
	}

	/**
	 * @see com.diamondq.reactions.api.PrepResultBuilder#stateValueByVariable(java.lang.String, java.lang.String)
	 */
	@Override
	public PrepResultBuilder<RT> stateValueByVariable(String pState, String pVariable) {
		mRequiredStates.add(new StateValueVariableCriteria(pState, pVariable, true));
		return this;
	}

	/**
	 * Finish this param and return back to the job
	 *
	 * @return the job builder
	 */
	@Override
	public JobBuilder build() {
		mJobSetup.addPrepResult(this);
		return mJobSetup;
	}
}

package com.diamondq.reactions.api.impl;

import com.diamondq.reactions.api.JobBuilder;
import com.diamondq.reactions.api.VariableBuilder;

import org.checkerframework.checker.nullness.qual.Nullable;

public class VariableBuilderImpl<VT> extends CommonBuilderImpl<VT, VariableBuilder<VT>> implements VariableBuilder<VT> {

	private final String		mVariableName;

	private boolean				mValueByResultName	= false;

	private @Nullable String	mValueByResultStateValue;

	public VariableBuilderImpl(JobBuilderImpl pJobSetup, Class<VT> pClass, String pVariableName) {
		super(pJobSetup, pClass);
		mVariableName = pVariableName;
	}

	/**
	 * Defines a missing state for this param
	 *
	 * @param pState the state
	 * @return the param builder
	 */
	@Override
	public VariableBuilderImpl<VT> missingState(String pState) {
		mRequiredStates.add(new StateCriteria(pState, false));
		return this;
	}

	/**
	 * Defines a missing state for this param
	 *
	 * @param pState the state
	 * @param pValue the value of the state
	 * @return the param builder
	 */
	@Override
	public VariableBuilderImpl<VT> missingStateEquals(String pState, String pValue) {
		mRequiredStates.add(new StateValueCriteria(pState, false, pValue));
		return this;
	}

	/**
	 * @see com.diamondq.reactions.api.VariableBuilder#valueByResultName()
	 */
	@Override
	public VariableBuilder<VT> valueByResultName() {
		mValueByResultName = true;
		return this;
	}

	public boolean isValueByResultName() {
		return mValueByResultName;
	}

	public String getVariableName() {
		return mVariableName;
	}

	/**
	 * @see com.diamondq.reactions.api.VariableBuilder#valueByResultStateValue(java.lang.String)
	 */
	@Override
	public VariableBuilder<VT> valueByResultStateValue(String pState) {
		mValueByResultStateValue = pState;
		return this;
	}

	public @Nullable String getValueByResultStateValue() {
		return mValueByResultStateValue;
	}

	/**
	 * Finish this param and return back to the job
	 *
	 * @return the job builder
	 */
	@Override
	public JobBuilder build() {
		mJobSetup.addVariable(this);
		return mJobSetup;
	}

}

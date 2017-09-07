package com.diamondq.reactions.api.impl;

import com.diamondq.reactions.api.JobBuilder;
import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.api.VariableBuilder;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class VariableBuilderImpl<VT> extends CommonBuilderImpl<VT, VariableBuilder<VT>> implements VariableBuilder<VT> {

	private final String							mVariableName;

	private boolean									mValueByResultName	= false;

	private @Nullable String						mValueByResultStateValue;

	private @Nullable Function<JobParamsBuilder, ?>	mValueByInput;

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
	 * @see com.diamondq.reactions.api.VariableBuilder#valueByInput(java.util.function.Function)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <A extends JobParamsBuilder, B> VariableBuilder<VT> valueByInput(Function<A, B> pSupplier) {
		mValueByInput = (Function<JobParamsBuilder, B>) pSupplier;
		return this;
	}

	public @Nullable Function<JobParamsBuilder, ?> getValueByInput() {
		return mValueByInput;
	}

	/**
	 * Finish this param and return back to the job
	 *
	 * @return the job builder
	 */
	@Override
	public JobBuilder build() {

		/* If we're defining the variable based on a state, then make sure the type is a String, since that's the only kind supported */

		if ((mValueByResultStateValue != null) && (mClass.equals(String.class) == false))
			throw new IllegalStateException("Only Variables of type String.class can be used to define a valueByResultStateValue");

		mJobSetup.addVariable(this);
		return mJobSetup;
	}

}

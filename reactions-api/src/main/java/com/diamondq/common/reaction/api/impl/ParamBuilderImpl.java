package com.diamondq.common.reaction.api.impl;

import com.diamondq.common.reaction.api.JobBuilder;
import com.diamondq.common.reaction.api.JobParamsBuilder;
import com.diamondq.common.reaction.api.ParamBuilder;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ParamBuilderImpl<PT> extends CommonBuilderImpl<PT, ParamBuilder<PT>> implements ParamBuilder<PT> {

	private @Nullable String						mValueByVariable;

	private @Nullable Function<JobParamsBuilder, ?>	mValueByInput;

	public ParamBuilderImpl(JobBuilderImpl pJobSetup, Class<PT> pClass) {
		super(pJobSetup, pClass);
	}

	/**
	 * Defines a missing state for this param
	 * 
	 * @param pState the state
	 * @return the param builder
	 */
	@Override
	public ParamBuilderImpl<PT> missingState(String pState) {
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
	public ParamBuilderImpl<PT> missingStateEquals(String pState, String pValue) {
		mRequiredStates.add(new StateValueCriteria(pState, false, pValue));
		return this;
	}

	@Override
	public ParamBuilderImpl<PT> stateByVariable(String pVariable) {
		mRequiredStates.add(new StateVariableCriteria(pVariable, true));
		return this;
	}

	/**
	 * @see com.diamondq.common.reaction.api.ParamBuilder#valueByVariable(java.lang.String)
	 */
	@Override
	public ParamBuilder<PT> valueByVariable(String pVariableName) {
		mValueByVariable = pVariableName;
		return this;
	}

	public @Nullable String getValueByVariable() {
		return mValueByVariable;
	}

	/**
	 * @see com.diamondq.common.reaction.api.ParamBuilder#valueByInput(java.util.function.Function)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <A extends JobParamsBuilder, B> ParamBuilder<PT> valueByInput(Function<A, B> pSupplier) {
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
		mJobSetup.addParam(this);
		return mJobSetup;
	}

}

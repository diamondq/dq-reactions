package com.diamondq.common.reaction.api.impl;

import com.diamondq.common.reaction.api.CommonBuilder;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This common abstract class represents builders for triggers, results and parameters.
 * 
 * @param <T> the class the job is associated with
 * @param <AS> the actual builder class (used for fluent style methods)
 */
public abstract class CommonBuilderImpl<T, AS extends CommonBuilder<T, AS>> implements CommonBuilder<T, AS> {

	protected JobBuilderImpl		mJobSetup;

	protected Class<T>				mClass;

	protected Set<StateCriteria>	mRequiredStates;

	protected Set<VariableCriteria>	mVariables;

	protected @Nullable String		mName;

	protected CommonBuilderImpl(JobBuilderImpl pJobSetup, Class<T> pClass) {
		mJobSetup = pJobSetup;
		mClass = pClass;
		mRequiredStates = new HashSet<>();
		mVariables = new HashSet<>();
	}

	/**
	 * Defines the name
	 * 
	 * @param pName the name
	 * @return the builder
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AS name(String pName) {
		mName = pName;
		return (AS) this;
	}

	/**
	 * Defines a required state
	 * 
	 * @param pState the state
	 * @return the builder
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AS state(String pState) {
		mRequiredStates.add(new StateCriteria(pState, true));
		return (AS) this;
	}

	/**
	 * Defines a required state
	 * 
	 * @param pState the state
	 * @param pValue the value of the state
	 * @return the builder
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AS stateEquals(String pState, String pValue) {
		mRequiredStates.add(new StateValueCriteria(pState, true, pValue));
		return (AS) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AS variable(String pState) {
		mVariables.add(new VariableCriteria(pState, pState));
		return (AS) this;
	}

	/**
	 * @see com.diamondq.common.reaction.api.CommonBuilder#isTransient()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AS isTransient() {
		mRequiredStates.add(new StateValueCriteria("persistent", true, "false"));
		return (AS) this;
	}

	/**
	 * @see com.diamondq.common.reaction.api.CommonBuilder#isPersistent()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AS isPersistent() {
		mRequiredStates.add(new StateValueCriteria("persistent", true, "true"));
		return (AS) this;
	}

	public Class<T> getParamClass() {
		return mClass;
	}

	public Set<StateCriteria> getRequiredStates() {
		return mRequiredStates;
	}

	public Set<VariableCriteria> getVariables() {
		return mVariables;
	}

	public @Nullable String getName() {
		return mName;
	}
}

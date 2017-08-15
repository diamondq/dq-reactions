package com.diamondq.common.reaction.api.impl;

import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Consumer4;
import com.diamondq.common.lambda.interfaces.Consumer5;
import com.diamondq.common.lambda.interfaces.Consumer6;
import com.diamondq.common.lambda.interfaces.Consumer7;
import com.diamondq.common.lambda.interfaces.Consumer8;
import com.diamondq.common.lambda.interfaces.Consumer9;
import com.diamondq.common.lambda.interfaces.Function0;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.lambda.interfaces.Function4;
import com.diamondq.common.lambda.interfaces.Function5;
import com.diamondq.common.lambda.interfaces.Function6;
import com.diamondq.common.lambda.interfaces.Function7;
import com.diamondq.common.lambda.interfaces.Function8;
import com.diamondq.common.lambda.interfaces.Function9;
import com.diamondq.common.reaction.api.JobBuilder;
import com.diamondq.common.reaction.api.JobContext;
import com.diamondq.common.reaction.api.JobDefinition;
import com.diamondq.common.reaction.api.JobInfo;
import com.diamondq.common.reaction.api.JobParamsBuilder;
import com.diamondq.common.reaction.api.ParamBuilder;
import com.diamondq.common.reaction.api.PrepResultBuilder;
import com.diamondq.common.reaction.api.ResultBuilder;
import com.diamondq.common.reaction.api.TriggerBuilder;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This is a builder used to define and set up a job
 */
public class JobBuilderImpl implements JobBuilder {

	private JobContext											mJobContext;

	private @Nullable MethodWrapper								mMethod;

	private @Nullable String									mName;

	private Set<ParamBuilderImpl<?>>							mParams;

	private Set<ResultBuilderImpl<?>>							mResults;

	private Set<PrepResultBuilderImpl<?>>						mPrepResults;

	private Set<TriggerBuilderImpl<?>>							mTriggers;

	private @Nullable JobInfo<?, ? extends JobParamsBuilder>	mInfo;

	public JobBuilderImpl(JobContext pJobContext) {
		mJobContext = pJobContext;
		mParams = new HashSet<>();
		mResults = new HashSet<>();
		mPrepResults = new HashSet<>();
		mTriggers = new HashSet<>();
	}

	/* ********************************************************************** */
	/* METHODS */
	/* ********************************************************************** */

	@Override
	public <T> JobBuilderImpl method(Consumer1<T> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2> JobBuilderImpl method(Consumer2<T1, T2> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3> JobBuilderImpl method(Consumer3<T1, T2, T3> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4> JobBuilderImpl method(Consumer4<T1, T2, T3, T4> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5> JobBuilderImpl method(Consumer5<T1, T2, T3, T4, T5> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, T6> JobBuilderImpl method(Consumer6<T1, T2, T3, T4, T5, T6> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, T7> JobBuilderImpl method(Consumer7<T1, T2, T3, T4, T5, T6, T7> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, T7, T8> JobBuilderImpl method(Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9> JobBuilderImpl method(
		Consumer9<T1, T2, T3, T4, T5, T6, T7, T8, T9> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <R> JobBuilderImpl method(Function0<R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T, R> JobBuilderImpl method(Function1<T, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, R> JobBuilderImpl method(Function2<T1, T2, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, R> JobBuilderImpl method(Function3<T1, T2, T3, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, R> JobBuilderImpl method(Function4<T1, T2, T3, T4, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, R> JobBuilderImpl method(Function5<T1, T2, T3, T4, T5, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, R> JobBuilderImpl method(Function6<T1, T2, T3, T4, T5, T6, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, T7, R> JobBuilderImpl method(Function7<T1, T2, T3, T4, T5, T6, T7, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, T7, T8, R> JobBuilderImpl method(
		Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> JobBuilderImpl method(
		Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	/* name */

	@Override
	public JobBuilderImpl name(String pName) {
		mName = pName;
		return this;
	}

	public @Nullable String getName() {
		return mName;
	}

	/* info */

	@Override
	public <T extends JobInfo<?, ?>> JobBuilderImpl info(@NonNull T pJobInfo) {
		mInfo = pJobInfo;
		return this;
	}

	public @Nullable JobInfo<?, ? extends JobParamsBuilder> getInfo() {
		return mInfo;
	}

	/* Params */

	/**
	 * Define a new parameter for this job. The parameter is defined by the class. NOTE: If there are multiple
	 * parameters that have the same type, you must use the ... When complete defining the param, use the
	 * ParamBuilder.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the param setup builder
	 */
	@Override
	public <PT> ParamBuilder<PT> param(Class<PT> pClass) {
		return new ParamBuilderImpl<PT>(this, pClass);
	}

	public <PT> void addParam(ParamBuilder<PT> pParamSetup) {
		if (pParamSetup instanceof ParamBuilderImpl == false)
			throw new IllegalArgumentException("Only ParamBuilderImpl is supported");
		mParams.add((ParamBuilderImpl<PT>) pParamSetup);
	}

	/* Results */

	/**
	 * Define a result this job. The result is defined by the class. NOTE: If there are multiple results that have the
	 * same type, you must use the ... When complete defining the result, use the ResultBuilder.build() method to return
	 * back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the result setup builder
	 */
	@Override
	public <RT> ResultBuilder<RT> result(Class<RT> pClass) {
		return new ResultBuilderImpl<RT>(this, pClass);
	}

	public <RT> void addResult(ResultBuilder<RT> pResultSetup) {
		if (pResultSetup instanceof ResultBuilderImpl == false)
			throw new IllegalArgumentException("Only ResultBuilderImpl is supported");
		mResults.add((ResultBuilderImpl<RT>) pResultSetup);
	}

	/* Prep Results */

	/**
	 * Define a result this job. The result is defined by the class. NOTE: If there are multiple results that have the
	 * same type, you must use the ... When complete defining the result, use the ResultBuilder.build() method to return
	 * back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the result setup builder
	 */
	@Override
	public <RT> PrepResultBuilder<RT> prepResult(Class<RT> pClass) {
		return new PrepResultBuilderImpl<RT>(this, pClass);
	}

	public <RT> void addPrepResult(PrepResultBuilder<RT> pPrepResultSetup) {
		if (pPrepResultSetup instanceof PrepResultBuilderImpl == false)
			throw new IllegalArgumentException("Only PrepResultBuilderImpl is supported");
		mPrepResults.add((PrepResultBuilderImpl<RT>) pPrepResultSetup);
	}

	/* Triggers */

	/**
	 * Define a trigger for this job. The trigger is defined by the class. When complete defining the result, use the
	 * TriggerBuilder.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the trigger setup builder
	 */
	@Override
	public <TT> TriggerBuilder<TT> trigger(Class<TT> pClass) {
		return new TriggerBuilderImpl<TT>(this, false, pClass);
	}

	/**
	 * Define a trigger for this job. The trigger is defined by the class. When complete defining the result, use the
	 * TriggerBuilder.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the trigger setup builder
	 */
	@Override
	public <TT> TriggerBuilder<TT> triggerCollection(Class<TT> pClass) {
		return new TriggerBuilderImpl<TT>(this, true, pClass);
	}

	public <TT> void addTrigger(TriggerBuilder<TT> pTriggerSetup) {
		if (pTriggerSetup instanceof TriggerBuilderImpl == false)
			throw new IllegalArgumentException("Only TriggerBuilderImpl is supported");
		mTriggers.add((TriggerBuilderImpl<TT>) pTriggerSetup);
	}

	/* Guards */

	@Override
	public JobBuilderImpl guard(Function1<?, Boolean> pGuardFunction) {
		return this;
	}

	@Override
	public JobDefinition build() {
		return mJobContext.build(this);
	}

	/* Getters */

	public JobContext getJobContext() {
		return mJobContext;
	}

	public @Nullable MethodWrapper getMethod() {
		return mMethod;
	}

	public Set<ParamBuilderImpl<?>> getParams() {
		return mParams;
	}

	public Set<ResultBuilderImpl<?>> getResults() {
		return mResults;
	}

	public Set<PrepResultBuilderImpl<?>> getPrepResults() {
		return mPrepResults;
	}

	public Set<TriggerBuilderImpl<?>> getTriggers() {
		return mTriggers;
	}

}

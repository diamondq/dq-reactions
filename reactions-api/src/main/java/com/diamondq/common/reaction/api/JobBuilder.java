package com.diamondq.common.reaction.api;

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
import com.diamondq.common.reaction.api.impl.JobBuilderImpl;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This is a builder used to define and set up a job
 */
public interface JobBuilder {

	/* ********************************************************************** */
	/* METHODS */
	/* ********************************************************************** */

	public <T> JobBuilder method(Consumer1<T> pConsumer);

	public <T1, T2> JobBuilder method(Consumer2<T1, T2> pConsumer);

	public <T1, T2, T3> JobBuilder method(Consumer3<T1, T2, T3> pConsumer);

	public <T1, T2, T3, T4> JobBuilder method(Consumer4<T1, T2, T3, T4> pConsumer);

	public <T1, T2, T3, T4, T5> JobBuilder method(Consumer5<T1, T2, T3, T4, T5> pConsumer);

	public <T1, T2, T3, T4, T5, T6> JobBuilder method(Consumer6<T1, T2, T3, T4, T5, T6> pConsumer);

	public <T1, T2, T3, T4, T5, T6, T7> JobBuilder method(Consumer7<T1, T2, T3, T4, T5, T6, T7> pConsumer);

	public <T1, T2, T3, T4, T5, T6, T7, T8> JobBuilder method(Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> pConsumer);

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9> JobBuilder method(
		Consumer9<T1, T2, T3, T4, T5, T6, T7, T8, T9> pConsumer);

	public <R> JobBuilder method(Function0<R> pConsumer);

	public <T, R> JobBuilder method(Function1<T, R> pConsumer);

	public <T1, T2, R> JobBuilder method(Function2<T1, T2, R> pConsumer);

	public <T1, T2, T3, R> JobBuilder method(Function3<T1, T2, T3, R> pConsumer);

	public <T1, T2, T3, T4, R> JobBuilder method(Function4<T1, T2, T3, T4, R> pConsumer);

	public <T1, T2, T3, T4, T5, R> JobBuilder method(Function5<T1, T2, T3, T4, T5, R> pConsumer);

	public <T1, T2, T3, T4, T5, T6, R> JobBuilder method(Function6<T1, T2, T3, T4, T5, T6, R> pConsumer);

	public <T1, T2, T3, T4, T5, T6, T7, R> JobBuilder method(Function7<T1, T2, T3, T4, T5, T6, T7, R> pConsumer);

	public <T1, T2, T3, T4, T5, T6, T7, T8, R> JobBuilder method(
		Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> pConsumer);

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> JobBuilder method(
		Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> pConsumer);

	/* name */

	public JobBuilderImpl name(String pName);

	/* info */

	public <T extends JobInfo<?, ?>> JobBuilderImpl info(@NonNull T pJobInfo);

	/* Params */

	/**
	 * Define a new parameter for this job. The parameter is defined by the class. NOTE: If there are multiple
	 * parameters that have the same type, you must use the ... When complete defining the param, use the
	 * ParamBuilder.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the param setup builder
	 */
	public <PT> ParamBuilder<PT> param(Class<PT> pClass);

	/* Results */

	/**
	 * Define a result this job. The result is defined by the class. NOTE: If there are multiple results that have the
	 * same type, you must use the ... When complete defining the result, use the ResultBuilder.build() method to return
	 * back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the result setup builder
	 */
	public <RT> ResultBuilder<RT> result(Class<RT> pClass);

	/* Prep Results */

	/**
	 * Define a result this job. The result is defined by the class. NOTE: If there are multiple results that have the
	 * same type, you must use the ... When complete defining the result, use the ResultBuilder.build() method to return
	 * back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the result setup builder
	 */
	public <RT> PrepResultBuilder<RT> prepResult(Class<RT> pClass);

	/* Triggers */

	/**
	 * Define a trigger for this job. The trigger is defined by the class. When complete defining the result, use the
	 * TriggerBuilder.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the trigger setup builder
	 */
	public <TT> TriggerBuilder<TT> trigger(Class<TT> pClass);

	/**
	 * Define a trigger for this job. The trigger is defined by the class. When complete defining the result, use the
	 * TriggerBuilder.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the trigger setup builder
	 */
	public <TT> TriggerBuilder<TT> triggerCollection(Class<TT> pClass);

	/* Guards */

	public JobBuilder guard(Function1<?, Boolean> pGuardFunction);

	public JobDefinition build();
}

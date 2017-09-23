package com.diamondq.reactions.api;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.interfaces.Consumer1;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ReactionsEngine {

	/**
	 * Issue a request to retrieve a result. The result requested is based on the result class. All @ConfigureReaction's
	 * are scanned to see if they can provide the necessary result, and what their dependencies are. This occurs
	 * recursively. Some jobs make take significant time before they complete (or error).
	 *
	 * @param pResultClass the interested result
	 * @return a future that indicates when the result is available
	 */
	public <T> ExtendedCompletableFuture<T> submit(Class<T> pResultClass);

	/**
	 * Submit a specific job.
	 *
	 * @param pJob
	 * @param pBuilder
	 * @return a future
	 */
	public <RESULT, JPB extends JobParamsBuilder, T extends JobInfo<RESULT, JPB>> ExtendedCompletableFuture<RESULT> submit(
		T pJob, JPB pBuilder);

	/**
	 * Creates a tracker that calls the given consumer functions whenever a matching job is created or destroyed.
	 *
	 * @param pJob the job
	 * @param pBuilder the job parameters
	 * @param pName the name
	 * @param pOnCreation called on creation
	 * @param pOnDestruction called on destruction
	 * @return the token that can be used to cancel this tracker
	 */
	public <RESULT, JPB extends JobParamsBuilder, T extends JobInfo<RESULT, JPB>> String on(T pJob, JPB pBuilder,
		@Nullable String pName, @Nullable Consumer1<RESULT> pOnCreation, @Nullable Consumer1<RESULT> pOnDestruction);

	/**
	 * Cancels a tracker with the token provided by the on() method
	 *
	 * @param pToken the token to cancel
	 */
	public void cancelOn(String pToken);

	/**
	 * Finds a known job by the jobInfo class
	 *
	 * @param pJobInfoClass the job info class
	 * @return the job info or null
	 */
	public <T extends JobInfo<?, ? extends JobParamsBuilder>> T findMandatoryJob(Class<T> pJobInfoClass);

	/* model changes */

	/* Collection model changes */

	public <@NonNull T> ExtendedCompletableFuture<@Nullable Void> addToCollection(T pRecord, Action pAction,
		String pName, Map<String, String> pStates);
}

package com.diamondq.common.reaction.api;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Engine {

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

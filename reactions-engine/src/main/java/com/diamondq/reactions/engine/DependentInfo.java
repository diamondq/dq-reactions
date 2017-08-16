package com.diamondq.reactions.engine;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Tracks information about the dependent needed for a given Parameter within a specific instance of a job.
 */
public class DependentInfo {

	/**
	 * If {@link #isResolved} is true, then this is the resolved value for this dependent, and no additional queries
	 * should be run for this Job execution.
	 */
	public volatile @Nullable Object			resolvedValue;

	/**
	 * True if the value has been resolved or false if it still is yet to be resolved
	 */
	public volatile boolean						isResolved;

	/**
	 * The list of possible jobs to use to resolve this dependent
	 */
	public volatile List<@NonNull JobRequest>	jobs;

	/**
	 * The current job # that is being executed.
	 */
	public volatile int							currentJob;

	public volatile @Nullable Throwable			jobError;

	public DependentInfo() {
		jobs = ImmutableList.of();
		currentJob = -1;
	}

}

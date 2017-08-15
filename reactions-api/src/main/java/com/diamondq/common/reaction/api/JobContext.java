package com.diamondq.common.reaction.api;

public interface JobContext {

	/**
	 * Creates a new JobBuilder
	 * 
	 * @return the builder
	 */
	public JobBuilder newJobBuilder();

	/**
	 * This is an internal method that should never be called by user code. It's only present here to create a clean
	 * separation between the API and Implementation projects.
	 * 
	 * @param pJobBuilder the builder
	 * @return the definition
	 */
	public JobDefinition build(JobBuilder pJobBuilder);

	/**
	 * Registers a new job definition
	 * 
	 * @param pDefinition the definition
	 */
	public void registerJob(JobDefinition pDefinition);

}

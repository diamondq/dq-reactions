package com.diamondq.reactions.api;

public interface JobInfo<RESULT, JPB extends JobParamsBuilder> {

	/**
	 * Create a new params builder
	 * 
	 * @return the builder
	 */
	public JPB newParamsBuilder();
}

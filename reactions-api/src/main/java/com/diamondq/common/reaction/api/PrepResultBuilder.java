package com.diamondq.common.reaction.api;

public interface PrepResultBuilder<RT> extends CommonResultBuilder<RT, PrepResultBuilder<RT>> {

	public PrepResultBuilder<RT> stateByVariable(String pVariable);

	/**
	 * Finish this param and return back to the job
	 * 
	 * @return the job builder
	 */
	public JobBuilder build();
}

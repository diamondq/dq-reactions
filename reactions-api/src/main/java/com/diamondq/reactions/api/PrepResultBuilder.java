package com.diamondq.reactions.api;

public interface PrepResultBuilder<RT> extends CommonResultBuilder<RT, PrepResultBuilder<RT>> {

	public PrepResultBuilder<RT> stateByVariable(String pVariable);

	public PrepResultBuilder<RT> stateValueByVariable(String pState, String pVariable);

	/**
	 * Finish this param and return back to the job
	 *
	 * @return the job builder
	 */
	public JobBuilder build();
}

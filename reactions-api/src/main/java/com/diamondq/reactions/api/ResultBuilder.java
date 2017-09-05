package com.diamondq.reactions.api;

public interface ResultBuilder<RT> extends CommonResultBuilder<RT, ResultBuilder<RT>> {

	/**
	 * Finish this param and return back to the job
	 *
	 * @return the job builder
	 */
	public JobBuilder build();

}

package com.diamondq.common.reaction.api;

public interface ResultBuilder<RT> extends CommonResultBuilder<RT, ResultBuilder<RT>> {

	/**
	 * Finish this param and return back to the job
	 * 
	 * @return the job builder
	 */
	public JobBuilder build();

	/**
	 * Defines a variable to hold the name of the result
	 * 
	 * @param pVariableName the name of the variable
	 * @return the builder
	 */
	public ResultBuilder<RT> nameByVariable(String pVariableName);
}

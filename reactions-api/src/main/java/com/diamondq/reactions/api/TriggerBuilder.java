package com.diamondq.reactions.api;

public interface TriggerBuilder<TT> extends CommonBuilder<TT, TriggerBuilder<TT>> {

	public TriggerBuilder<TT> action(Action pAction);

	/**
	 * Finish this trigger and return back to the job
	 * 
	 * @return the job builder
	 */
	public JobBuilder build();

}

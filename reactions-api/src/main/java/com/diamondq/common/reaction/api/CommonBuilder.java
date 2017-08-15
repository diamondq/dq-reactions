package com.diamondq.common.reaction.api;

/**
 * This common abstract class represents builders for triggers, results and parameters.
 * 
 * @param <T> the class the job is associated with
 * @param <AS> the actual builder class (used for fluent style methods)
 */
public interface CommonBuilder<T, AS extends CommonBuilder<T, AS>> {

	/**
	 * Defines the name
	 * 
	 * @param pName the name
	 * @return the builder
	 */
	public AS name(String pName);

	/**
	 * Defines a required state
	 * 
	 * @param pState the state
	 * @return the builder
	 */
	public AS state(String pState);

	/**
	 * Defines a required state
	 * 
	 * @param pState the state
	 * @param pValue the value of the state
	 * @return the builder
	 */
	public AS stateEquals(String pState, String pValue);

	/**
	 * A shortcut that sets the 'persistent=false' state
	 * 
	 * @return the builder
	 */
	public AS isTransient();

	/**
	 * A shortcut that sets the 'persistent=true' state
	 * 
	 * @return the builder
	 */
	public AS isPersistent();

	public AS variable(String pState);
}

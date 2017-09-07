package com.diamondq.reactions.api;

import java.util.function.Function;

public interface VariableBuilder<VT> extends CommonBuilder<VT, VariableBuilder<VT>> {

	/**
	 * Defines a missing state for this variableName
	 *
	 * @param pState the state
	 * @return the Variable builder
	 */
	public VariableBuilder<VT> missingState(String pState);

	/**
	 * Defines a missing state for this variableName
	 *
	 * @param pState the state
	 * @param pValue the value of the state
	 * @return the Variable builder
	 */
	public VariableBuilder<VT> missingStateEquals(String pState, String pValue);

	/**
	 * Defines the value of this variable based on the value of a state
	 *
	 * @param pState the state
	 * @return the Variable builder
	 */
	public VariableBuilder<VT> valueByResultStateValue(String pState);

	/**
	 * Defines the value of this variable based on the name of the result
	 *
	 * @return the Variable Builder
	 */
	public VariableBuilder<VT> valueByResultName();

	/**
	 * Defines that the value of the Variable is defined by a supplier function
	 *
	 * @param pSupplier the supplier
	 * @return the Variable builder
	 */
	public <A extends JobParamsBuilder, B> VariableBuilder<VT> valueByInput(Function<A, B> pSupplier);

	/**
	 * Finish this variableName and return back to the job
	 *
	 * @return the Variable builder
	 */
	public JobBuilder build();

}

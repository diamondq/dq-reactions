package com.diamondq.reactions.api;

import java.util.function.Function;

public interface ParamBuilder<PT> extends CommonBuilder<PT, ParamBuilder<PT>> {

	/**
	 * Defines a missing state for this param
	 *
	 * @param pState the state
	 * @return the param builder
	 */
	public ParamBuilder<PT> missingState(String pState);

	/**
	 * Defines a missing state for this param
	 *
	 * @param pState the state
	 * @param pValue the value of the state
	 * @return the param builder
	 */
	public ParamBuilder<PT> missingStateEquals(String pState, String pValue);

	/**
	 * Defines a state by the value of a variableName. It's the equivalent to calling state(RESOLVE_VARIABLE(pVariable))
	 *
	 * @param pVariable the variableName name
	 * @return the param builder
	 */
	public ParamBuilder<PT> stateByVariable(String pVariable);

	/**
	 * Defines a state value by the value of a variableName. It's the equivalent to calling stateEquals(pState,
	 * RESOLVE_VARIABLE(pVariable))
	 *
	 * @param pState the state
	 * @param pVariable the variableName name
	 * @return the param builder
	 */
	public ParamBuilder<PT> stateValueByVariable(String pState, String pVariable);

	/**
	 * Defines that the value of the param is defined by a variableName. NOTE: This is only supported for String.class
	 * based parameters.
	 *
	 * @param pVariableName the variableName name
	 * @return the param builder
	 */
	public ParamBuilder<PT> valueByVariable(String pVariableName);

	/**
	 * Defines that the value of the param is defined by a supplier function
	 *
	 * @param pSupplier the supplier
	 * @return the param builder
	 */
	public <A extends JobParamsBuilder, B> ParamBuilder<PT> valueByInput(Function<A, B> pSupplier);

	/**
	 * Defines that the value of the param is defined by what triggered this function
	 *
	 * @return the param builder
	 */
	public ParamBuilder<PT> valueByTrigger();

	/**
	 * Finish this param and return back to the job
	 *
	 * @return the job builder
	 */
	public JobBuilder build();

}

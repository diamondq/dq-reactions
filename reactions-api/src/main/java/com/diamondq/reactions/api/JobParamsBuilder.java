package com.diamondq.reactions.api;

public interface JobParamsBuilder {

	/**
	 * Allow the setting of parameters in a reflective way
	 *
	 * @param pKey the key
	 * @param pValue the value
	 */
	public void setParam(String pKey, Object pValue);

}

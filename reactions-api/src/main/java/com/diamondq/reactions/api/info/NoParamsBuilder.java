package com.diamondq.reactions.api.info;

import com.diamondq.reactions.api.JobParamsBuilder;

public class NoParamsBuilder implements JobParamsBuilder {

	/**
	 * @see com.diamondq.reactions.api.JobParamsBuilder#setParam(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setParam(String pKey, Object pValue) {
		throw new IllegalArgumentException("Unrecognized param key: " + pKey);
	}
}

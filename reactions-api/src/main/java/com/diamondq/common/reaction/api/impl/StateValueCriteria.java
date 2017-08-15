package com.diamondq.common.reaction.api.impl;

public class StateValueCriteria extends StateCriteria {

	public final String value;

	public StateValueCriteria(String pState, boolean pIsEqual, String pValue) {
		super(pState, pIsEqual);
		value = pValue;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("StateValueCriteria(");
		sb.append(state);
		sb.append(isEqual == true ? "=" : "!=");
		sb.append(value);
		sb.append(")");
		return sb.toString();
	}
}

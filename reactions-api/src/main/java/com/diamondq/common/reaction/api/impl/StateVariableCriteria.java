package com.diamondq.common.reaction.api.impl;

public class StateVariableCriteria extends StateCriteria {

	public StateVariableCriteria(String pState, boolean pIsEqual) {
		super(pState, pIsEqual);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("StateVariableCriteria(");
		sb.append(state);
		sb.append(isEqual == true ? "=" : "!=");
		sb.append(")");
		return sb.toString();
	}
}

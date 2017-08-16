package com.diamondq.reactions.api.impl;

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

	@Override
	public String getIdentifier() {
		StringBuilder sb = new StringBuilder();
		if (isEqual == false)
			sb.append('!');
		sb.append('{');
		sb.append(state);
		sb.append('}');
		return sb.toString();
	}
}

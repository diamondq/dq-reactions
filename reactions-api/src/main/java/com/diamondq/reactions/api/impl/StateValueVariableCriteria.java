package com.diamondq.reactions.api.impl;

public class StateValueVariableCriteria extends StateCriteria {

	public final String variableName;

	public StateValueVariableCriteria(String pState, String pVariable, boolean pIsEqual) {
		super(pState, pIsEqual);
		variableName = pVariable;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("StateValueVariableCriteria(");
		sb.append(state);
		sb.append(isEqual == true ? "=" : "!=");
		sb.append("{");
		sb.append(variableName);
		sb.append("})");
		return sb.toString();
	}

	/**
	 * @see com.diamondq.reactions.api.impl.StateCriteria#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		StringBuilder sb = new StringBuilder();
		sb.append(state);
		sb.append(isEqual == true ? "=" : "!=");
		sb.append('{');
		sb.append(variableName);
		sb.append('}');
		return sb.toString();
	}
}

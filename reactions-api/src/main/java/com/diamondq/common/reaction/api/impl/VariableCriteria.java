package com.diamondq.common.reaction.api.impl;

public class VariableCriteria extends StateCriteria {

	public final String	variableName;

	public VariableCriteria(String pState, String pVariableName) {
		super(pState, true);
		variableName = pVariableName;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VariableCriteria(");
		sb.append(state);
		sb.append('/');
		sb.append(variableName);
		sb.append(")");
		return sb.toString();
	}
}

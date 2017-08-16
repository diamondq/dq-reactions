package com.diamondq.reactions.api.impl;

public class StateCriteria {

	public final String		state;

	public final boolean	isEqual;

	public StateCriteria(String pState, boolean pIsEqual) {
		state = pState;
		isEqual = pIsEqual;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("StateCriteria(");
		sb.append(state);
		sb.append(isEqual == true ? "=" : "!=");
		sb.append(")");
		return sb.toString();
	}

	public String getIdentifier() {
		StringBuilder sb = new StringBuilder();
		if (isEqual == false)
			sb.append('!');
		sb.append(state);
		return sb.toString();
	}
}

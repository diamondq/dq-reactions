package com.diamondq.reactions.api;

public interface CommonResultBuilder<RT, AS extends CommonResultBuilder<RT, AS>> extends CommonBuilder<RT, AS> {

	public AS asParam();

}

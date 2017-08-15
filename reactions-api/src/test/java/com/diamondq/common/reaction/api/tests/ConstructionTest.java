package com.diamondq.common.reaction.api.tests;

import com.diamondq.common.reaction.api.JobContext;

import org.junit.Test;

public class ConstructionTest {

	public String simpleFunc(String pInput) {
		return pInput + "--";
	}

	public String simpleSupplier() {
		return "Basic";
	}

	@Test
	public void test() {
		JobContext jc = new MockJobContext();
		// @formatter:off
		jc.newJobBuilder().method(this::simpleFunc)
			.param(String.class)
				.state("State1")
				.build()
			.result(String.class)
				.state("State2")
				.build()
			.build();
		
		jc.newJobBuilder().method(this::simpleSupplier)
			.result(String.class)
				.state("State1")
				.build()
			.build();
		//@formatter:on
	}

}

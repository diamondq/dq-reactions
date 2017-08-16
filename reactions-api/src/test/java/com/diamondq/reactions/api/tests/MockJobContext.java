package com.diamondq.reactions.api.tests;

import com.diamondq.reactions.api.JobBuilder;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.JobDefinition;
import com.diamondq.reactions.api.impl.JobBuilderImpl;

import org.eclipse.jdt.annotation.NonNull;

public class MockJobContext implements JobContext {

	/**
	 * @see com.diamondq.reactions.api.JobContext#newJobBuilder()
	 */
	@Override
	public JobBuilder newJobBuilder() {
		return new JobBuilderImpl(this);
	}

	/**
	 * @see com.diamondq.reactions.api.JobContext#build(com.diamondq.reactions.api.JobBuilder)
	 */
	@Override
	public @NonNull JobDefinition build(@NonNull JobBuilder pJobBuilder) {
		return new MockJobDefinition(pJobBuilder);
	}

	/**
	 * @see com.diamondq.reactions.api.JobContext#registerJob(com.diamondq.reactions.api.JobDefinition)
	 */
	@Override
	public void registerJob(@NonNull JobDefinition pDefinition) {
		
	}

}

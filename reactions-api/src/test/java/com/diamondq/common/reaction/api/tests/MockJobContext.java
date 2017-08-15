package com.diamondq.common.reaction.api.tests;

import com.diamondq.common.reaction.api.JobBuilder;
import com.diamondq.common.reaction.api.JobContext;
import com.diamondq.common.reaction.api.JobDefinition;
import com.diamondq.common.reaction.api.impl.JobBuilderImpl;

import org.eclipse.jdt.annotation.NonNull;

public class MockJobContext implements JobContext {

	/**
	 * @see com.diamondq.common.reaction.api.JobContext#newJobBuilder()
	 */
	@Override
	public JobBuilder newJobBuilder() {
		return new JobBuilderImpl(this);
	}

	/**
	 * @see com.diamondq.common.reaction.api.JobContext#build(com.diamondq.common.reaction.api.JobBuilder)
	 */
	@Override
	public @NonNull JobDefinition build(@NonNull JobBuilder pJobBuilder) {
		return new MockJobDefinition(pJobBuilder);
	}

	/**
	 * @see com.diamondq.common.reaction.api.JobContext#registerJob(com.diamondq.common.reaction.api.JobDefinition)
	 */
	@Override
	public void registerJob(@NonNull JobDefinition pDefinition) {
		
	}

}

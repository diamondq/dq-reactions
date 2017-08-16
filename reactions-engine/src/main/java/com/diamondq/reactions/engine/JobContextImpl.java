package com.diamondq.reactions.engine;

import com.diamondq.reactions.api.JobBuilder;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.JobDefinition;
import com.diamondq.reactions.api.impl.JobBuilderImpl;
import com.diamondq.reactions.engine.definitions.JobDefinitionImpl;

public class JobContextImpl implements JobContext {

	public final EngineImpl mEngine;

	public JobContextImpl(EngineImpl pEngine) {
		mEngine = pEngine;
	}

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
	public JobDefinition build(JobBuilder pJobBuilder) {
		if (pJobBuilder instanceof JobBuilderImpl == false)
			throw new IllegalArgumentException("The provided JobBuilder must be a JobBuilderImpl");
		return new JobDefinitionImpl((JobBuilderImpl) pJobBuilder);
	}

	/**
	 * @see com.diamondq.reactions.api.JobContext#registerJob(com.diamondq.reactions.api.JobDefinition)
	 */
	@Override
	public void registerJob(JobDefinition pDefinition) {
		mEngine.registerJob(pDefinition);
	}

}

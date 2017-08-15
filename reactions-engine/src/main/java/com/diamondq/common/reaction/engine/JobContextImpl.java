package com.diamondq.common.reaction.engine;

import com.diamondq.common.reaction.api.JobBuilder;
import com.diamondq.common.reaction.api.JobContext;
import com.diamondq.common.reaction.api.JobDefinition;
import com.diamondq.common.reaction.api.impl.JobBuilderImpl;
import com.diamondq.common.reaction.engine.definitions.JobDefinitionImpl;

public class JobContextImpl implements JobContext {

	public final EngineImpl mEngine;

	public JobContextImpl(EngineImpl pEngine) {
		mEngine = pEngine;
	}

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
	public JobDefinition build(JobBuilder pJobBuilder) {
		if (pJobBuilder instanceof JobBuilderImpl == false)
			throw new IllegalArgumentException("The provided JobBuilder must be a JobBuilderImpl");
		return new JobDefinitionImpl((JobBuilderImpl) pJobBuilder);
	}

	/**
	 * @see com.diamondq.common.reaction.api.JobContext#registerJob(com.diamondq.common.reaction.api.JobDefinition)
	 */
	@Override
	public void registerJob(JobDefinition pDefinition) {
		mEngine.registerJob(pDefinition);
	}

}

package com.diamondq.common.reaction.engine.tests.simple_trigger;

import com.diamondq.common.reaction.api.ConfigureReaction;
import com.diamondq.common.reaction.api.JobContext;
import com.diamondq.common.reaction.api.info.AbstractNoParamsJobInfo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefineDependent extends AbstractNoParamsJobInfo {

	@ConfigureReaction
	public void setupJob(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder().method(this::getDependent).info(this)
			.result(OtherDependent.class).isTransient().name("other-dependent").build()
		.build());
		// @formatter:on
	}

	public OtherDependent getDependent() {
		return new OtherDependent();
	}
}

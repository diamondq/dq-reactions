package com.diamondq.reactions.engine.tests.simple_trigger;

import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.info.AbstractNoParamsJobInfo;

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

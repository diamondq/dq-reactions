package com.diamondq.common.reaction.engine.tests.simple_trigger;

import com.diamondq.common.reaction.api.Action;
import com.diamondq.common.reaction.api.ConfigureReaction;
import com.diamondq.common.reaction.api.JobContext;
import com.diamondq.common.reaction.api.info.AbstractNoParamsJobInfo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OnNewRecord extends AbstractNoParamsJobInfo {

	@ConfigureReaction
	public void setupJob(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder().method(this::modifyRecord).info(this)
			.triggerCollection(Record.class).action(Action.INSERT).build()
			.param(OtherDependent.class).isTransient().build()
		.build());
		// @formatter:on
	}

	public void modifyRecord(Record pRecord, OtherDependent pDependent) {
		pRecord.value = pRecord.value + pDependent.getSuffix();
	}
}

package com.diamondq.reactions.engine.tests.simple_trigger;

import com.diamondq.reactions.api.Action;
import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.info.AbstractNoParamsJobInfo;

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

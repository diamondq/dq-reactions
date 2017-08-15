package com.diamondq.common.reaction.engine;

import com.diamondq.common.reaction.api.JobParamsBuilder;
import com.diamondq.common.reaction.engine.definitions.JobDefinitionImpl;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class JobRequest {

	public final JobDefinitionImpl			jobDefinition;

	public final @Nullable Object			triggerObject;

	public final Map<String, String>		variables;

	public final @Nullable JobParamsBuilder	paramsBuilder;

	public JobRequest(JobDefinitionImpl pJobDefinition, @Nullable Object pTriggerObject) {
		this(pJobDefinition, pTriggerObject, Collections.emptyMap(), null);
	}

	public JobRequest(JobDefinitionImpl pJobDefinition, @Nullable Object pTriggerObject, Map<String, String> pVariables,
		@Nullable JobParamsBuilder pBuilder) {
		super();
		jobDefinition = pJobDefinition;
		triggerObject = pTriggerObject;
		variables = ImmutableMap.copyOf(pVariables);
		paramsBuilder = pBuilder;
	}

}

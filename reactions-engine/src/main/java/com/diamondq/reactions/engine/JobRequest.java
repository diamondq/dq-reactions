package com.diamondq.reactions.engine;

import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.engine.definitions.JobDefinitionImpl;
import com.diamondq.reactions.engine.definitions.ParamDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class JobRequest {

	public final JobDefinitionImpl						jobDefinition;

	public final @Nullable Object						triggerObject;

	public final Map<String, String>					variables;

	public final @Nullable JobParamsBuilder				paramsBuilder;

	public final Map<ParamDefinition<?>, DependentInfo>	executingByParam;

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
		executingByParam = Maps.newConcurrentMap();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getIdentifier();
	}
	
	public String getIdentifier() {
		StringBuilder sb = new StringBuilder();
		sb.append(jobDefinition.getShortName());
		sb.append('(');
		boolean isFirst = true;
		for (ParamDefinition<?> p : jobDefinition.params) {
			if (isFirst == true)
				isFirst = false;
			else
				sb.append(", ");
			DependentInfo dependentInfo = executingByParam.get(p);
			if (dependentInfo == null) {
				sb.append('<');
				sb.append(p.getIdentifier());
				sb.append('>');
			}
			else {
				if (dependentInfo.isResolved == true) {
					if (dependentInfo.resolvedValue == null)
						sb.append("null");
					else {
						if (p.clazz.isAssignableFrom(String.class))
							sb.append('"').append(dependentInfo.resolvedValue).append('"');
						else
							sb.append(dependentInfo.resolvedValue);
					}
				}
				else {
					sb.append('<');
					sb.append(p.getIdentifier());
					sb.append('>');
				}
			}
		}
		sb.append(')');
		return sb.toString();
	}
}

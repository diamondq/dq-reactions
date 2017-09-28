package com.diamondq.reactions.engine;

import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.api.impl.StateCriteria;
import com.diamondq.reactions.engine.definitions.DependentDefinition;
import com.diamondq.reactions.engine.definitions.JobDefinitionImpl;
import com.diamondq.reactions.engine.definitions.ParamDefinition;
import com.diamondq.reactions.engine.definitions.ResultDefinition;
import com.diamondq.reactions.engine.definitions.VariableDefinition;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class JobRequest {

	public final JobDefinitionImpl							jobDefinition;

	public final @Nullable Object							triggerObject;

	public final @Nullable JobParamsBuilder					paramsBuilder;

	public final Map<DependentDefinition<?>, DependentInfo>	executingByParam;

	/**
	 * Defines a mapping between a variable name and the VariableDefinition. Usually used to find the associated mapping
	 * in the executingByParam to actually find the resolved value of the Variable
	 */
	public final Map<String, VariableDefinition<?>>			variableMap;

	/**
	 * The 'resolved' result name. Since the name could either come from the definition or from a variable, a place is
	 * need to store the actual result name.
	 */
	public final @Nullable String							resultName;

	public final Set<StateCriteria>							resultStates;

	public final @Nullable Consumer1<Object>				onCreation;

	public final @Nullable Consumer1<Object>				onDestruction;

	public JobRequest(JobDefinitionImpl pJobDefinition, @Nullable Object pTriggerObject, @Nullable String pResultName,
		Set<StateCriteria> pResultStates) {
		this(pJobDefinition, pTriggerObject, null, pResultName, pResultStates, null, null);
	}

	public JobRequest(JobDefinitionImpl pJobDefinition, @Nullable Object pTriggerObject,
		@Nullable JobParamsBuilder pBuilder, @Nullable String pResultName, Set<StateCriteria> pResultStates,
		@Nullable Consumer1<Object> pOnCreation, @Nullable Consumer1<Object> pOnDestruction) {
		super();
		jobDefinition = pJobDefinition;
		triggerObject = pTriggerObject;
		paramsBuilder = pBuilder;
		executingByParam = Maps.newConcurrentMap();
		variableMap = Maps.newConcurrentMap();
		resultName = pResultName;
		resultStates = ImmutableSet.copyOf(pResultStates);
		onCreation = pOnCreation;
		onDestruction = pOnDestruction;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getIdentifier();
	}

	public String getIdentifyingName() {
		StringBuilder sb = new StringBuilder();
		sb.append(jobDefinition.getShortName());
		boolean displayResultName = true;
		String tResultName = resultName;
		if (tResultName != null) {
			if (jobDefinition.results.size() == 1) {
				ResultDefinition<?> rd = Iterables.getFirst(jobDefinition.results, null);
				if (rd != null) {
					if (tResultName.equals(rd.name) == true)
						displayResultName = false;
				}
			}
		}
		else
			displayResultName = false;
		boolean displayResultStates = true;
		if (jobDefinition.results.size() == 1) {
			ResultDefinition<?> rd = Iterables.getFirst(jobDefinition.results, null);
			if (rd != null) {
				if (rd.requiredStates.equals(resultStates) == true)
					displayResultStates = false;
			}
		}
		if ((displayResultStates == true) || (displayResultName == true)) {
			sb.append('[');
			if (displayResultName == true)
				sb.append("name=").append(tResultName);
			if (displayResultStates == true) {
				if (displayResultName == true)
					sb.append(", ");
				sb.append("states=");
				sb.append(resultStates);
			}
			sb.append(']');
		}
		if (jobDefinition.variables.isEmpty() == false) {
			sb.append('{');
			boolean isFirst = true;
			for (VariableDefinition<?> p : jobDefinition.variables) {
				if (isFirst == true)
					isFirst = false;
				else
					sb.append(", ");
				sb.append(p.getIdentifier());
			}
			sb.append('}');
		}

		return sb.toString();
	}

	public String getIdentifier() {
		StringBuilder sb = new StringBuilder();
		sb.append(jobDefinition.getShortName());
		boolean displayResultName = true;
		String tResultName = resultName;
		if (tResultName != null) {
			if (jobDefinition.results.size() == 1) {
				ResultDefinition<?> rd = Iterables.getFirst(jobDefinition.results, null);
				if (rd != null) {
					if (tResultName.equals(rd.name) == true)
						displayResultName = false;
				}
			}
		}
		else
			displayResultName = false;
		boolean displayResultStates = true;
		if (jobDefinition.results.size() == 1) {
			ResultDefinition<?> rd = Iterables.getFirst(jobDefinition.results, null);
			if (rd != null) {
				if (rd.requiredStates.equals(resultStates) == true)
					displayResultStates = false;
			}
		}
		if ((displayResultStates == true) || (displayResultName == true)) {
			sb.append('[');
			if (displayResultName == true)
				sb.append("name=").append(tResultName);
			if (displayResultStates == true) {
				if (displayResultName == true)
					sb.append(", ");
				sb.append("states=");
				sb.append(resultStates);
			}
			sb.append(']');
		}
		if (jobDefinition.variables.isEmpty() == false) {
			sb.append('{');
			boolean isFirst = true;
			for (VariableDefinition<?> p : jobDefinition.variables) {
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
						sb.append(p.variableName);
						sb.append('=');
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
			sb.append('}');
		}
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

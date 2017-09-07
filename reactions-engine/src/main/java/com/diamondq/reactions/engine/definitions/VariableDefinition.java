package com.diamondq.reactions.engine.definitions;

import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.api.impl.StateCriteria;
import com.diamondq.reactions.api.impl.VariableBuilderImpl;

import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class VariableDefinition<T> extends DependentDefinition<T> {

	public final String										variableName;

	public final boolean									valueByResultName;

	public final @Nullable String							valueByResultStateValue;

	public final @Nullable Function<JobParamsBuilder, ?>	valueByInput;

	VariableDefinition(VariableBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getName(), pBuilder.getVariableName(),
			pBuilder.isValueByResultName(), pBuilder.getValueByResultStateValue(), pBuilder.getValueByInput());
	}

	public VariableDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, @Nullable String pName,
		String pVariableName, boolean pValueByResultName, @Nullable String pValueByResultStateValue,
		@Nullable Function<JobParamsBuilder, ?> pValueByInput) {
		super(pClazz, pRequiredStates, pName);
		variableName = pVariableName;
		if ((pValueByResultName == true) && (pValueByResultStateValue != null))
			throw new IllegalStateException("Only one of valueByResultName and valueByResultStateValue can be set");
		valueByResultName = pValueByResultName;
		valueByResultStateValue = pValueByResultStateValue;
		valueByInput = pValueByInput;
	}

	/**
	 * @see com.diamondq.reactions.engine.definitions.DependentDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		StringBuilder sb = new StringBuilder();
		sb.append(variableName);
		sb.append('/');
		if (name != null) {
			sb.append(name);
			sb.append('/');
		}
		sb.append(clazz.getSimpleName());
		boolean isFirst = true;
		for (StateCriteria sc : requiredStates) {
			if (isFirst == true) {
				isFirst = false;
				sb.append('?');
			}
			else
				sb.append('&');
			sb.append(sc.getIdentifier());
		}
		return sb.toString();
	}
}

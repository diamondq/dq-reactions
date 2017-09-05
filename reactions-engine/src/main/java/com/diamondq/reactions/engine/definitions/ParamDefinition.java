package com.diamondq.reactions.engine.definitions;

import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.api.impl.ParamBuilderImpl;
import com.diamondq.reactions.api.impl.StateCriteria;

import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ParamDefinition<T> extends DependentDefinition<T> {

	public final @Nullable String							valueByVariable;

	public final @Nullable Function<JobParamsBuilder, ?>	valueByInput;

	public final boolean									valueByTrigger;

	ParamDefinition(ParamBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getName(), pBuilder.getValueByVariable(),
			pBuilder.getValueByInput(), pBuilder.isValueByTrigger());
	}

	public ParamDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, @Nullable String pName,
		@Nullable String pValueByVariable, @Nullable Function<JobParamsBuilder, ?> pValueByInput,
		boolean pValueByTrigger) {
		super(pClazz, pRequiredStates, pName);
		valueByVariable = pValueByVariable;
		valueByInput = pValueByInput;
		valueByTrigger = pValueByTrigger;
	}

}

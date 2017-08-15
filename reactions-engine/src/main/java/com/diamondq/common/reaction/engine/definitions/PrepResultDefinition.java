package com.diamondq.common.reaction.engine.definitions;

import com.diamondq.common.reaction.api.impl.PrepResultBuilderImpl;
import com.diamondq.common.reaction.api.impl.StateCriteria;
import com.diamondq.common.reaction.api.impl.VariableCriteria;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class PrepResultDefinition<T> {

	public final Class<T>				clazz;

	public final Set<StateCriteria>		requiredStates;

	public final Set<VariableCriteria>	variables;

	public final @Nullable String		name;

	public final boolean				resultIsParam;

	PrepResultDefinition(PrepResultBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getVariables(), pBuilder.getName(),
			pBuilder.getResultIsParam());
	}

	public PrepResultDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, Set<VariableCriteria> pVariables,
		@Nullable String pName, boolean pResultIsParam) {
		clazz = pClazz;
		requiredStates = ImmutableSet.copyOf(pRequiredStates);
		variables = ImmutableSet.copyOf(pVariables);
		name = pName;
		resultIsParam = pResultIsParam;
	}

}

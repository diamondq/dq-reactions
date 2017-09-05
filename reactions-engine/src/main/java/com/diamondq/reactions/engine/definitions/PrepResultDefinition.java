package com.diamondq.reactions.engine.definitions;

import com.diamondq.reactions.api.impl.PrepResultBuilderImpl;
import com.diamondq.reactions.api.impl.StateCriteria;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class PrepResultDefinition<T> {

	public final Class<T>			clazz;

	public final Set<StateCriteria>	requiredStates;

	public final @Nullable String	name;

	public final boolean			resultIsParam;

	PrepResultDefinition(PrepResultBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getName(), pBuilder.getResultIsParam());
	}

	public PrepResultDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, @Nullable String pName,
		boolean pResultIsParam) {
		clazz = pClazz;
		requiredStates = ImmutableSet.copyOf(pRequiredStates);
		name = pName;
		resultIsParam = pResultIsParam;
	}

}

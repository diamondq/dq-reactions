package com.diamondq.common.reaction.engine.definitions;

import com.diamondq.common.reaction.api.Action;
import com.diamondq.common.reaction.api.impl.StateCriteria;
import com.diamondq.common.reaction.api.impl.TriggerBuilderImpl;
import com.diamondq.common.reaction.api.impl.VariableCriteria;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TriggerDefinition<T> {

	public final Class<T>				clazz;

	public final Set<StateCriteria>		requiredStates;

	public final Set<VariableCriteria>	variables;

	public final @Nullable String		name;

	public final Action					action;

	public final boolean				isCollection;

	TriggerDefinition(TriggerBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getVariables(), pBuilder.getName(),
			pBuilder.getAction(), pBuilder.isCollection());
	}

	public TriggerDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, Set<VariableCriteria> pVariables,
		@Nullable String pName, Action pAction, boolean pIsCollection) {
		clazz = pClazz;
		requiredStates = ImmutableSet.copyOf(pRequiredStates);
		variables = ImmutableSet.copyOf(pVariables);
		name = pName;
		action = pAction;
		isCollection = pIsCollection;
	}

}

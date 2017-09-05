package com.diamondq.reactions.engine.definitions;

import com.diamondq.reactions.api.Action;
import com.diamondq.reactions.api.impl.StateCriteria;
import com.diamondq.reactions.api.impl.TriggerBuilderImpl;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TriggerDefinition<T> {

	public final Class<T>			clazz;

	public final Set<StateCriteria>	requiredStates;

	public final @Nullable String	name;

	public final Action				action;

	public final boolean			isCollection;

	TriggerDefinition(TriggerBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getName(), pBuilder.getAction(),
			pBuilder.isCollection());
	}

	public TriggerDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, @Nullable String pName,
		Action pAction, boolean pIsCollection) {
		clazz = pClazz;
		requiredStates = ImmutableSet.copyOf(pRequiredStates);
		name = pName;
		action = pAction;
		isCollection = pIsCollection;
	}

}

package com.diamondq.reactions.engine.definitions;

import com.diamondq.reactions.api.impl.ResultBuilderImpl;
import com.diamondq.reactions.api.impl.StateCriteria;
import com.diamondq.reactions.api.impl.StateValueCriteria;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ResultDefinition<T> {

	public final Class<T>			clazz;

	public final boolean			isStored;

	public final @Nullable Boolean	isPersistent;

	public final Set<StateCriteria>	requiredStates;

	public final @Nullable String	name;

	public final boolean			resultIsParam;

	ResultDefinition(ResultBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getName(), pBuilder.getResultIsParam());
	}

	public ResultDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, @Nullable String pName,
		boolean pResultIsParam) {
		clazz = pClazz;
		requiredStates = ImmutableSet.copyOf(pRequiredStates);
		name = pName;
		resultIsParam = pResultIsParam;
		boolean stored = false;
		Boolean persist = null;
		for (StateCriteria criteria : pRequiredStates)
			if ("persistent".equals(criteria.state)) {
				stored = true;
				if (criteria instanceof StateValueCriteria) {
					StateValueCriteria svc = (StateValueCriteria) criteria;
					if (svc.isEqual == true)
						if ("true".equals(svc.value)) {
							persist = true;
							break;
						}
						else if ("false".equals(svc.value)) {
							persist = false;
							break;
						}
				}
			}

		isStored = stored;
		isPersistent = persist;
	}

}

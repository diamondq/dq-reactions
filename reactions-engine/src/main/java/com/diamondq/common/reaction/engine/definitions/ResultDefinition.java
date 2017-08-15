package com.diamondq.common.reaction.engine.definitions;

import com.diamondq.common.reaction.api.impl.ResultBuilderImpl;
import com.diamondq.common.reaction.api.impl.StateCriteria;
import com.diamondq.common.reaction.api.impl.StateValueCriteria;
import com.diamondq.common.reaction.api.impl.VariableCriteria;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ResultDefinition<T> {

	public final Class<T>				clazz;

	public final @Nullable Boolean		persistent;

	public final Set<StateCriteria>		requiredStates;

	public final Set<VariableCriteria>	variables;

	public final @Nullable String		name;

	public final @Nullable String		nameByVariable;

	public final boolean				resultIsParam;

	ResultDefinition(ResultBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getVariables(), pBuilder.getName(),
			pBuilder.getNameByVariable(), pBuilder.getResultIsParam());
	}

	public ResultDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, Set<VariableCriteria> pVariables,
		@Nullable String pName, @Nullable String pNameByVariable, boolean pResultIsParam) {
		clazz = pClazz;
		requiredStates = ImmutableSet.copyOf(pRequiredStates);
		variables = ImmutableSet.copyOf(pVariables);
		name = pName;
		nameByVariable = pNameByVariable;
		resultIsParam = pResultIsParam;
		Boolean persist = null;
		for (StateCriteria criteria : pRequiredStates)
			if ("persistent".equals(criteria.state)) {
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

		persistent = persist;
	}

}

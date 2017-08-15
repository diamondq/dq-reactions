package com.diamondq.common.reaction.engine.definitions;

import com.diamondq.common.lambda.Memoizer;
import com.diamondq.common.reaction.api.JobParamsBuilder;
import com.diamondq.common.reaction.api.impl.ParamBuilderImpl;
import com.diamondq.common.reaction.api.impl.StateCriteria;
import com.diamondq.common.reaction.api.impl.StateValueCriteria;
import com.diamondq.common.reaction.api.impl.VariableCriteria;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ParamDefinition<T> {

	public final Class<T>									clazz;

	public final Set<StateCriteria>							requiredStates;

	public final @Nullable Boolean							persistent;

	public final Set<VariableCriteria>						variables;

	public final @Nullable String							name;

	public final @Nullable String							valueByVariable;

	public final @Nullable Function<JobParamsBuilder, ?>	valueByInput;

	private final Memoizer									mMemoizer;

	ParamDefinition(ParamBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getVariables(), pBuilder.getName(),
			pBuilder.getValueByVariable(), pBuilder.getValueByInput());
	}

	public ParamDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, Set<VariableCriteria> pVariables,
		@Nullable String pName, @Nullable String pValueByVariable,
		@Nullable Function<JobParamsBuilder, ?> pValueByInput) {
		mMemoizer = new Memoizer();
		clazz = pClazz;
		requiredStates = ImmutableSet.copyOf(pRequiredStates);
		variables = ImmutableSet.copyOf(pVariables);
		name = pName;
		valueByVariable = pValueByVariable;
		valueByInput = pValueByInput;
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

	public String getShortName() {
		return mMemoizer.memoize(() -> {
			StringBuilder sb = new StringBuilder();
			if (ParamDefinition.this.name != null) {
				sb.append(ParamDefinition.this.name);
				sb.append('/');
			}
			sb.append(ParamDefinition.this.clazz.getName());
			return sb.toString();
		}, "shortName");
	}

}

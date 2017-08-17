package com.diamondq.reactions.engine.definitions;

import com.diamondq.common.lambda.Memoizer;
import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.api.impl.ParamBuilderImpl;
import com.diamondq.reactions.api.impl.StateCriteria;
import com.diamondq.reactions.api.impl.StateValueCriteria;
import com.diamondq.reactions.api.impl.VariableCriteria;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ParamDefinition<T> {

	public final Class<T>									clazz;

	public final Set<StateCriteria>							requiredStates;

	public final boolean									isStored;

	public final @Nullable Boolean							isPersistent;

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
		boolean stored = false;
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

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getIdentifier();
	}

	/**
	 * Returns a string that can be used to identify this parameter. It's usually used as part of an error message.
	 * 
	 * @return the identifier.
	 */
	public String getIdentifier() {
		StringBuilder sb = new StringBuilder();
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

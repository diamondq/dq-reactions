package com.diamondq.reactions.engine.definitions;

import com.diamondq.reactions.api.impl.ResultBuilderImpl;
import com.diamondq.reactions.api.impl.StateCriteria;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ResultDefinition<T> extends DependentDefinition<T> {

	public final boolean resultIsParam;

	ResultDefinition(ResultBuilderImpl<T> pBuilder) {
		this(pBuilder.getParamClass(), pBuilder.getRequiredStates(), pBuilder.getName(), pBuilder.getResultIsParam());
	}

	public ResultDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, @Nullable String pName,
		boolean pResultIsParam) {
		super(pClazz, pRequiredStates, pName);
		resultIsParam = pResultIsParam;
	}

}

package com.diamondq.reactions.engine.definitions;

import com.diamondq.common.lambda.Memoizer;
import com.diamondq.reactions.api.impl.StateCriteria;
import com.diamondq.reactions.api.impl.StateValueCriteria;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DependentDefinition<T> {

	public final Class<T>			clazz;

	public final Set<StateCriteria>	requiredStates;

	public final boolean			isStored;

	public final @Nullable Boolean	isPersistent;

	public final @Nullable String	name;

	protected final Memoizer		mMemoizer;

	public DependentDefinition(Class<T> pClazz, Set<StateCriteria> pRequiredStates, @Nullable String pName) {
		mMemoizer = new Memoizer();
		clazz = pClazz;
		requiredStates = ImmutableSet.copyOf(pRequiredStates);
		name = pName;
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
			if (DependentDefinition.this.name != null) {
				sb.append(DependentDefinition.this.name);
				sb.append('/');
			}
			sb.append(DependentDefinition.this.clazz.getName());
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

package com.diamondq.common.reaction.engine.evals;

import com.diamondq.common.reaction.api.impl.StateCriteria;
import com.diamondq.common.reaction.api.impl.StateValueCriteria;
import com.diamondq.common.reaction.api.impl.StateVariableCriteria;
import com.diamondq.common.reaction.api.impl.VariableCriteria;
import com.diamondq.common.reaction.engine.definitions.JobDefinitionImpl;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class NameNode {

	public final String												name;

	private final SetMultimap<StateCriteria[], JobDefinitionImpl>	jobsByState;

	private final Set<JobDefinitionImpl>							jobsNoState;

	public NameNode(String pName) {
		name = pName;
		jobsByState = Multimaps.synchronizedSetMultimap(HashMultimap.create());
		jobsNoState = Sets.newHashSet();
	}

	public Set<StateCriteria[]> getCriterias() {
		return jobsByState.keySet();
	}

	public Set<JobDefinitionImpl> getNoCriteriaJobs() {
		return jobsNoState;
	}

	public @Nullable Set<JobDefinitionImpl> getJobsByCriteria(StateCriteria[] pCriterias) {
		return jobsByState.get(pCriterias);
	}

	public void addCriteria(Iterable<StateCriteria> pCriteria, JobDefinitionImpl pJobDefinition) {
		ArrayList<StateCriteria> list = Lists.newArrayList(pCriteria);
		Collections.sort(list, new Comparator<StateCriteria>() {

			@Override
			public int compare(StateCriteria pO1, StateCriteria pO2) {
				int result = pO1.state.compareTo(pO2.state);
				if (result != 0)
					return result;
				if (pO1.isEqual != pO2.isEqual) {
					if (pO1.isEqual == true)
						return -1;
					return 1;
				}
				if (pO1 instanceof StateValueCriteria) {
					if (pO2 instanceof StateValueCriteria == false) {
						if ((pO2 instanceof StateVariableCriteria) || (pO2 instanceof VariableCriteria))
							return -1;
						return 1;
					}
					return ((StateValueCriteria) pO1).value.compareTo(((StateValueCriteria) pO2).value);
				}
				else if (pO1 instanceof StateVariableCriteria) {
					if (pO2 instanceof StateVariableCriteria)
						return 0;
					if (pO2 instanceof VariableCriteria)
						return -1;
					return 1;
				}
				else if (pO1 instanceof VariableCriteria) {
					if (pO2 instanceof VariableCriteria == false)
						return 1;
					return ((VariableCriteria) pO1).variableName.compareTo(((VariableCriteria) pO2).variableName);
				}
				else {
					if ((pO2 instanceof StateValueCriteria) || (pO2 instanceof StateVariableCriteria)
						|| (pO2 instanceof VariableCriteria))
						return -1;
					return 0;
				}
			}
		});
		StateCriteria[] array = list.toArray(new StateCriteria[0]);
		jobsByState.put(array, pJobDefinition);
	}
}

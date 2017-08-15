package com.diamondq.common.reaction.engine;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.QueryBuilder;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.WhereOperator;
import com.diamondq.common.reaction.api.Action;
import com.diamondq.common.reaction.api.impl.StateCriteria;
import com.diamondq.common.reaction.api.impl.StateValueCriteria;
import com.diamondq.common.reaction.api.impl.StateVariableCriteria;
import com.diamondq.common.reaction.engine.definitions.ParamDefinition;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Store {

	private final Toolkit				mToolkit;

	public final Scope					mScope;

	public final StructureDefinition	mDataBitsDef;

	public final StructureDefinition	mDataDef;

	public final StructureDefinition	mDataStatesDef;

	public Store(Toolkit pToolkit, String pScopeName) {
		mToolkit = pToolkit;
		Scope scope = mToolkit.getScope(pScopeName);
		if (scope == null)
			throw new IllegalArgumentException("The mandatory mScope " + pScopeName + " was not found");
		mScope = scope;

		StructureDefinition sd;

		/* Data Object States */

		sd = pToolkit.createNewStructureDefinition(mScope, "dataStates");
		sd = sd.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "key", PropertyType.String)
			.setPrimaryKey(true).setFinal(true));
		sd = sd.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "value", PropertyType.String));
		sd = sd.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "data", PropertyType.PropertyRef)
			.addKeyword(CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT));
		pToolkit.writeStructureDefinition(mScope, sd);
		StructureDefinition dataStatesDef = pToolkit.lookupStructureDefinitionByName(mScope, "dataStates");
		if (dataStatesDef == null)
			throw new IllegalArgumentException("Unable to find the previously created dataStates structure definition");
		mDataStatesDef = dataStatesDef;
		StructureDefinitionRef dataStatesRef = dataStatesDef.getReference();

		/* Data Object */

		sd = pToolkit.createNewStructureDefinition(mScope, "data");

		sd = sd.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "type", PropertyType.String)
			.setPrimaryKey(true).setPrimaryKeyOrder(1));
		sd = sd.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "name", PropertyType.String)
			.setPrimaryKey(true).setPrimaryKeyOrder(2));
		sd = sd.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "part", PropertyType.String)
			.setPrimaryKey(true).setPrimaryKeyOrder(3));
		sd = sd
			.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "valueRef", PropertyType.StructureRef));
		sd = sd
			.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "states", PropertyType.StructureRefList)
				.addReferenceType(dataStatesRef).addKeyword(CommonKeywordKeys.CONTAINER,
					CommonKeywordValues.CONTAINER_CHILDREN));
		pToolkit.writeStructureDefinition(mScope, sd);
		StructureDefinition dataDef = pToolkit.lookupStructureDefinitionByName(mScope, "data");
		if (dataDef == null)
			throw new IllegalArgumentException("Unable to find the previously created data structure definition");
		mDataDef = dataDef;

		/* Generic Data Value */

		sd = pToolkit.createNewStructureDefinition(mScope, "dataBits");
		sd = sd.addPropertyDefinition(
			pToolkit.createNewPropertyDefinition(mScope, "id", PropertyType.String).setPrimaryKey(true).setFinal(true));
		sd = sd.addPropertyDefinition(pToolkit.createNewPropertyDefinition(mScope, "bits", PropertyType.Binary));
		pToolkit.writeStructureDefinition(mScope, sd);
		StructureDefinition dataBitsDef = pToolkit.lookupStructureDefinitionByName(mScope, "dataBits");
		if (dataBitsDef == null)
			throw new IllegalArgumentException("Unable to find the previously created dataBits structure definition");
		mDataBitsDef = dataBitsDef;
	}

	public <T> void persist(T pRecord, Action pAction, String pType, String pName, Map<String, String> pStates) {

		StructureRef recordRef;
		String partId;
		if (pRecord instanceof Structure) {

			Structure recordStructure = (Structure) pRecord;
			partId = (String) recordStructure.lookupMandatoryPropertyByName("part").getValue(recordStructure);
			recordRef = recordStructure.getReference();
			mToolkit.writeStructure(mScope, recordStructure);

		}
		else {

			partId = UUID.randomUUID().toString();

			Structure recordStructure = mToolkit.createNewStructure(mScope, mDataBitsDef);
			recordStructure =
				recordStructure.updateProperty(recordStructure.lookupMandatoryPropertyByName("id").setValue(partId));
			recordStructure =
				recordStructure.updateProperty(recordStructure.lookupMandatoryPropertyByName("bits").setValue(pRecord));
			recordRef = recordStructure.getReference();
			mToolkit.writeStructure(mScope, recordStructure);
		}

		/* Now create the data object */

		Structure dataStructure = mToolkit.createNewStructure(mScope, mDataDef);
		dataStructure =
			dataStructure.updateProperty(dataStructure.lookupMandatoryPropertyByName("type").setValue(pType));
		dataStructure =
			dataStructure.updateProperty(dataStructure.lookupMandatoryPropertyByName("name").setValue(pName));
		dataStructure =
			dataStructure.updateProperty(dataStructure.lookupMandatoryPropertyByName("part").setValue(partId));
		dataStructure =
			dataStructure.updateProperty(dataStructure.lookupMandatoryPropertyByName("valueRef").setValue(recordRef));
		StructureRef ref = dataStructure.getReference();
		mToolkit.writeStructure(mScope, dataStructure);

		dataStructure = ref.resolve();
		if (dataStructure == null)
			throw new IllegalArgumentException("The structure couldn't be found");
		PropertyRef<@Nullable Object> statesRef =
			mToolkit.createPropertyRef(mScope, dataStructure.lookupMandatoryPropertyByName("states"), dataStructure);

		/* Clear out any old states */

		@SuppressWarnings("unchecked")
		List<StructureRef> statesList =
			(List<StructureRef>) dataStructure.lookupMandatoryPropertyByName("states").getValue(dataStructure);
		if (statesList != null) {
			for (StructureRef r : statesList) {
				Structure oldState = r.resolve();
				if (oldState != null)
					mToolkit.deleteStructure(mScope, oldState);
			}
		}

		/* Next, write the states */

		for (Map.Entry<String, String> pair : pStates.entrySet()) {
			Structure stateStructure = mToolkit.createNewStructure(mScope, mDataStatesDef);
			stateStructure = stateStructure
				.updateProperty(stateStructure.lookupMandatoryPropertyByName("key").setValue(pair.getKey()));
			stateStructure = stateStructure
				.updateProperty(stateStructure.lookupMandatoryPropertyByName("value").setValue(pair.getValue()));
			stateStructure =
				stateStructure.updateProperty(stateStructure.lookupMandatoryPropertyByName("data").setValue(statesRef));
			mToolkit.writeStructure(mScope, stateStructure);
		}

	}

	public Set<Object> resolve(ParamDefinition<?> pParam) {
		QueryBuilder queryBuilder =
			mToolkit.createNewQueryBuilder(mScope).andWhereConstant("type", WhereOperator.eq, pParam.clazz.getName());
		String name = pParam.name;
		if (name != null)
			queryBuilder = queryBuilder.andWhereConstant("name", WhereOperator.eq, name);
		List<Structure> list = mToolkit.lookupStructuresByQuery(mScope, mDataDef, queryBuilder, Collections.emptyMap());

		Set<Object> results = Sets.newHashSet();
		/*
		 * Now, additionally filter by the StateCriteria. NOTE: At some point, it would be more efficient to move the
		 * StateCriteria into the model query, but that's not implemented at this point.
		 */

		for (Structure s : list) {

			/* Build the map of all states */

			Map<String, String> states = Maps.newHashMap();

			@SuppressWarnings("unchecked")
			List<StructureRef> stateRefs = (List<StructureRef>) s.lookupMandatoryPropertyByName("states").getValue(s);
			if (stateRefs != null)
				for (StructureRef sr : stateRefs) {
					Structure stateStructure = sr.resolve();
					if (stateStructure != null) {
						String key =
							(String) stateStructure.lookupMandatoryPropertyByName("key").getValue(stateStructure);
						String value =
							(String) stateStructure.lookupMandatoryPropertyByName("value").getValue(stateStructure);
						if ((key != null) && (value != null))
							states.put(key, value);
					}
				}

			/* Now filter */

			boolean match = true;
			for (StateCriteria sc : pParam.requiredStates) {
				if (sc instanceof StateValueCriteria) {
					StateValueCriteria svc = (StateValueCriteria) sc;
					String testValue = states.get(svc.state);
					if (testValue == null) {
						if (svc.isEqual == true) {
							match = false;
							break;
						}
						continue;
					}
					if (testValue.equals(svc.value)) {
						if (svc.isEqual == false) {
							match = false;
							break;
						}
						continue;
					}
					if (svc.isEqual == true) {
						match = false;
						break;
					}
					continue;
				}
				else if (sc instanceof StateVariableCriteria) {
					throw new UnsupportedOperationException();
				}
				else {
					/* It won't be a VariableCriteria, since those are stored separately */

					String testValue = states.get(sc.state);
					if (testValue == null) {
						if (sc.isEqual == true) {
							match = false;
							break;
						}
						continue;
					}
					if (sc.isEqual == false) {
						match = false;
						break;
					}
					continue;
				}
			}

			if (match == true) {
				StructureRef valueRef = (StructureRef) s.lookupMandatoryPropertyByName("valueRef").getValue(s);
				if (valueRef != null) {
					Structure bits = valueRef.resolve();
					if (bits != null) {
						Object data = bits.lookupMandatoryPropertyByName("bits").getValue(bits);
						if (data != null) {
							if (pParam.clazz.isInstance(data))
								results.add(data);
							else {
								// TODO Convert it back from byte[]
								throw new UnsupportedOperationException();
							}
						}
					}
				}
			}
		}
		return results;
	}

}

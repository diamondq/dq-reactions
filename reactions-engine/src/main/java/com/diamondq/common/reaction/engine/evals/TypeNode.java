package com.diamondq.common.reaction.engine.evals;

import com.diamondq.common.reaction.engine.EngineImpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TypeNode {

	public final String								type;

	private final ConcurrentMap<String, NameNode>	names;

	public TypeNode(String pType) {
		type = pType;
		names = new ConcurrentHashMap<>();
	}

	public NameNode getOrAddName(String pName) {
		NameNode nameNode = names.get(pName);
		if (nameNode != null)
			return nameNode;
		NameNode newNameNode = new NameNode(pName);
		if ((nameNode = names.putIfAbsent(pName, newNameNode)) == null)
			nameNode = newNameNode;
		return nameNode;
	}

	public NameNode getOrAddVariableName(String pNameByVariable) {
		NameNode nameNode = names.get(EngineImpl.sVARIABLE);
		if (nameNode == null) {
			NameNode newNameNode = new VariableNameNode();
			if ((nameNode = names.putIfAbsent(EngineImpl.sVARIABLE, newNameNode)) == null)
				nameNode = newNameNode;
		}
		VariableNameNode vnn = (VariableNameNode)nameNode;
		return vnn.getOrAddVariable(pNameByVariable);
	}

	public @Nullable NameNode getName(String pName) {
		return names.get(pName);
	}

}
